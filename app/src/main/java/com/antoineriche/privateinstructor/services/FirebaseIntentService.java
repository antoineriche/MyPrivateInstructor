package com.antoineriche.privateinstructor.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.activities.SnapshotFragment;
import com.antoineriche.privateinstructor.asynctasks.FirebaseTasks;
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.beans.DatabaseItem;
import com.antoineriche.privateinstructor.beans.Devoir;
import com.antoineriche.privateinstructor.beans.Location;
import com.antoineriche.privateinstructor.beans.Pupil;
import com.antoineriche.privateinstructor.beans.Snapshot;
import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.database.DevoirTable;
import com.antoineriche.privateinstructor.database.LocationTable;
import com.antoineriche.privateinstructor.database.MyDatabase;
import com.antoineriche.privateinstructor.database.PupilTable;
import com.antoineriche.privateinstructor.notifications.AbstractNotification;
import com.antoineriche.privateinstructor.utils.DateUtils;
import com.antoineriche.privateinstructor.utils.FirebaseUtils;
import com.antoineriche.privateinstructor.utils.PreferencesUtils;
import com.antoineriche.privateinstructor.utils.SnapshotFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FirebaseIntentService extends IntentService {

    private static final String TAG = FirebaseIntentService.class.getSimpleName();

    public static final String FIB_TASKS = "FIB_TASKS";
    public static final String FIB_CHECK_SYNCHRONIZATION = "FIB_CHECK_SYNCHRONIZATION";
    public static final String FIB_SCHEDULE_SNAPSHOT = "FIB_SCHEDULE_SNAPSHOT";
    public static final String FIB_SAVE_NEW_SNAPSHOT = "FIB_SAVE_NEW_SNAPSHOT";
    public static final String FIB_GET_LAST_SNAPSHOT_DATE = "FIB_GET_LAST_SNAPSHOT_DATE";
    public static final String FIB_GET_SNAPSHOTS = "FIB_GET_SNAPSHOTS";
    public static final String FIB_REPLACE_DATABASE = "FIB_REPLACE_DATABASE";
    public static final String FIB_NEW_DATABASE = "FIB_NEW_DATABASE";
    public static final String FIB_CREATE_SNAPSHOT = "FIB_CREATE_SNAPSHOT";

    private FirebaseAuth mAuth;

    public FirebaseIntentService() {
        super(FirebaseIntentService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "onHandleIntent: " + intent);
        boolean hasTasks = intent.hasExtra(FIB_TASKS);
        Log.e(TAG, "Intent has tasks: " + hasTasks);
        FirebaseUser user = mAuth.getCurrentUser();
        boolean userConnected = user != null;
        Log.e(TAG, "User connected: " + userConnected);

        if(userConnected && hasTasks) {

            user.getIdToken(true).addOnCompleteListener(task -> {
                Log.e(TAG, "Refresh token? " + task.isSuccessful());
                if (task.isSuccessful()) { dispatchJobs(intent, user); }
                else { Log.e(TAG, "Exception: " + task.getException().getMessage(), task.getException()); }
            });

        }
    }

    private void dispatchJobs(Intent intent, FirebaseUser user) {
        String userUid = user.getUid();
        List<String> tasks = Arrays.asList(intent.getStringArrayExtra(FIB_TASKS));

        if (tasks.contains(FIB_CHECK_SYNCHRONIZATION)) {
            checkSynchronization(this, userUid, new FirebaseTasks.SynchronizeListener() {

                @Override
                public void startSynchronization(String pReferenceKey) {
                    Log.e(TAG, String.format(Locale.FRANCE, "Start %s synchronization", pReferenceKey));
                }

                @Override
                public void progress(String pReferenceKey, double percent) {
                    Log.e(TAG, String.format(Locale.FRANCE, "%s synchronization progress: ", pReferenceKey, percent));
                }

                @Override
                public void failToSaveItemInFirebase(String pReferenceKey, long pItemId, String pError) {
                    Log.e(TAG, String.format(Locale.FRANCE, "Fail to save %s (id=%f) on Firebase.", pReferenceKey, pItemId, pError));
                }

                @Override
                public void failToRemoveItemOfFirebase(String pReferenceKey, long pItemId, String pError) {
                    Log.e(TAG, String.format(Locale.FRANCE, "Fail to remove %s (id=%f) of Firebase.", pReferenceKey, pItemId, pError));
                }

                @Override
                public void failToUpdateItemInFirebase(String pReferenceKey, long pItemId, String pError) {
                    Log.e(TAG, String.format(Locale.FRANCE, "Fail to update %s (id=%f) in Firebase.", pReferenceKey, pItemId, pError));
                }

                @Override
                public void itemsSuccessfullySynchronized(String pReferenceKey) {
                    Log.e(TAG, String.format(Locale.FRANCE, "%s successfully synchronized.", pReferenceKey));
                }
            });
        }
        if (tasks.contains(FIB_SCHEDULE_SNAPSHOT)) {
            scheduleSnapshot(this, userUid);
        }
        if (tasks.contains(FIB_SAVE_NEW_SNAPSHOT)) {
            saveNewSnapshot(userUid);
        }
        if (tasks.contains(FIB_GET_LAST_SNAPSHOT_DATE)) {
            getLastSnapshot(userUid);
        }
        if (tasks.contains(FIB_GET_SNAPSHOTS)) {
            getSnapshots(userUid);
        }
        if (tasks.contains(FIB_REPLACE_DATABASE)) {
            String snapshotName = intent.getStringExtra(FIB_NEW_DATABASE);
            boolean keepCopy = intent.getBooleanExtra(FIB_CREATE_SNAPSHOT, false);
            askForReplacingDatabaseBySnapshot(userUid, snapshotName, keepCopy);
        }
    }

    private void askForReplacingDatabaseBySnapshot(String pUserUid, String pSnapshotName, Boolean pCreateSnapshot) {
        Log.e(TAG, "ReplaceDatabase by: " + pSnapshotName + ", makeCopy: " + pCreateSnapshot);

        SQLiteDatabase database = new MyDatabase(getApplicationContext(), null).getWritableDatabase();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SnapshotFragment.MyReceiver.SNAPSHOT_FRAGMENT);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);

        if(pCreateSnapshot){
            FirebaseUtils.createSnapshot(database, pUserUid, new FirebaseUtils.SaveSnapshotListener() {
                        @Override
                        public void snapshotSaved(Snapshot newSnapshot) {
                            Log.e(TAG, "Snapshot created.");
                            replaceDatabaseBySnapshot(pUserUid, pSnapshotName);
                        }

                        @Override
                        public void snapshotSavingFailed(String pError) {
                            Log.e(TAG, "Error while creating snapshot: " + pError);
                        }
                    }
            );
        } else {
            replaceDatabaseBySnapshot(pUserUid, pSnapshotName);
        }
    }

    private void replaceDatabaseBySnapshot(String pUserUid, String pSnapshotName){
        SQLiteDatabase database = new MyDatabase(getApplicationContext(), null).getWritableDatabase();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SnapshotFragment.MyReceiver.SNAPSHOT_FRAGMENT);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);

        FirebaseUtils.getSnapshotWithKey(pUserUid, pSnapshotName, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Snapshot newSnap = FirebaseUtils.extractSnapshotFromDataSnapshot(dataSnapshot);

                CourseTable.clearTable(database);
                newSnap.getCourses().forEach(c -> CourseTable.insertCourse(database, c));

                PupilTable.clearTable(database);
                newSnap.getPupils().forEach(p -> PupilTable.insertPupil(database, p));

                LocationTable.clearTable(database);
                newSnap.getLocations().forEach(l -> LocationTable.insertLocation(database, l));

                DevoirTable.clearTable(database);
                newSnap.getDevoirs().forEach(d -> DevoirTable.insertDevoir(database, d));

                broadcastIntent.putExtra(FIB_REPLACE_DATABASE, true);
                sendBroadcast(broadcastIntent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error while getting snapshot ("+pSnapshotName+").", databaseError.toException());
                broadcastIntent.putExtra(FIB_REPLACE_DATABASE, false);
                sendBroadcast(broadcastIntent);
            }
        });
    }

    private void scheduleSnapshot(Context pContext, String pUserUid){
        boolean snapshotsEnabled = PreferencesUtils.getBooleanPreferences(pContext, getString(R.string.pref_enable_firebase_snapshot));
        Log.e(TAG, "Snapshots enabled: " + snapshotsEnabled);

        if(snapshotsEnabled){

            FirebaseUtils.getLastSnapshot(pUserUid, new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String str = null;
                    for(DataSnapshot snap : dataSnapshot.getChildren()){ str = snap.getKey(); }
                    Date last = !TextUtils.isEmpty(str) ? SnapshotFactory.extractDateFromSnapshot(str) : new Date(0);
                    Log.e(TAG, "Last snapshot: " + last);

                    //FIXME use configuration
                    //long snapshotDelay = PreferencesUtils.getLongPreferences(getApplicationContext(),
                    //       getString(R.string.pref_firebase_snapshot_frequency));

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(last);
                    calendar.add(Calendar.DAY_OF_YEAR, 7);
                    calendar.setTime(DateUtils.getFirstSecondOfTheDay(calendar.getTime()));


                    //FIXME: Hack for test
                    long alarmTime = calendar.getTimeInMillis();
                    Log.e(TAG, "Next snapshot: " + new Date(alarmTime));
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent intentAlarm = new Intent(getApplicationContext(), NotificationReceiver.class);
                    intentAlarm.putExtra(NotificationReceiver.NOTIFICATION_CODE, AbstractNotification.SNAPSHOT_TIME);

                    alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime,
                            PendingIntent.getBroadcast(getApplicationContext(), AbstractNotification.SNAPSHOT_TIME,
                                    intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT)
                    );
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Error while saving snapshot.", databaseError.toException());
                }
            });
        }
    }

    private void saveNewSnapshot(String pUserUid){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intentAlarm = new Intent(getApplicationContext(), NotificationReceiver.class);
        SQLiteDatabase db = new MyDatabase(getApplicationContext(), null).getReadableDatabase();
        FirebaseUtils.createSnapshot(db, pUserUid, new FirebaseUtils.SaveSnapshotListener() {
            @Override
            public void snapshotSaved(Snapshot newSnapshot) {
                Log.e(TAG, "Snapshot created: " + newSnapshot.toString());
                long alarmTime = new Date().getTime();

                Bundle args = new Bundle();
                args.putSerializable(NotificationReceiver.ITEM, newSnapshot);
                intentAlarm.putExtra(NotificationReceiver.NOTIFICATION_CODE, AbstractNotification.SNAPSHOT_NEW_ONE);
                intentAlarm.putExtra(NotificationReceiver.NOTIFICATION_ARGS, args);

                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime,
                        PendingIntent.getBroadcast(getApplicationContext(), AbstractNotification.SNAPSHOT_NEW_ONE,
                                intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
            }

            @Override
            public void snapshotSavingFailed(String pError) {
                Log.e(TAG, "Error while creating snapshot: " + pError);
                long alarmTime = new Date().getTime();

                intentAlarm.putExtra(NotificationReceiver.NOTIFICATION_CODE, AbstractNotification.SNAPSHOT_FAILURE);
                intentAlarm.putExtra(NotificationReceiver.ERROR, pError);

                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime,
                        PendingIntent.getBroadcast(getApplicationContext(), AbstractNotification.SNAPSHOT_FAILURE,
                                intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
            }
        });
    }

    private void checkSynchronization(Context pContext, String pUserUid, FirebaseTasks.SynchronizeListener pListener){
        boolean synchronizationEnabled = PreferencesUtils.getBooleanPreferences(pContext, getString(R.string.pref_enable_firebase_synchronization));
        Log.e(TAG, "Synchronization enabled: " + synchronizationEnabled);

        if(synchronizationEnabled){

            FirebaseUtils.getUserDataFromFirebase(pUserUid, new FirebaseUtils.FirebaseListener() {

                @Override
                public void onRemoteItems(List<DatabaseItem> pRemoteItems, DatabaseReference pDatabaseReference) {
                    //noinspection unchecked
                    new FirebaseTasks.SynchronizeDatabases(pDatabaseReference,
                            new MyDatabase(pContext, null), pListener)
                            .execute(pRemoteItems);
                }

                @Override
                public void cancelled(String pError) {
                    Log.e(TAG, "Error while retrieving data: " + pError);
                }
            });
        }
    }

    private void getLastSnapshot(String pUserUid){
        Log.e(TAG, "getLastSnapshot");

        FirebaseUtils.getLastSnapshot(pUserUid, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String str = null;
                for(DataSnapshot snap : dataSnapshot.getChildren()){ str = snap.getKey(); }

                boolean dateIsNull = TextUtils.isEmpty(str);
                Log.e(TAG, "dateIsNull: " + dateIsNull);

                if(!dateIsNull) {
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(SnapshotFragment.MyReceiver.SNAPSHOT_FRAGMENT);
                    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    broadcastIntent.putExtra(FIB_GET_LAST_SNAPSHOT_DATE,
                            SnapshotFactory.extractDateFromSnapshot(str).getTime());
                    sendBroadcast(broadcastIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error while getting last snapshot date.", databaseError.toException());
            }
        });
    }

    private void getSnapshots(String pUserUid){
        Log.e(TAG, "getSnapshots");

        FirebaseUtils.getSnapshots(pUserUid, new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Snapshot> snapshots = new ArrayList<>();
                for(DataSnapshot snap : dataSnapshot.getChildren()) {
                    Date date = SnapshotFactory.extractDateFromSnapshot(snap.getKey());

                    List<Course> courses = snap.hasChild(FirebaseUtils.COURSE_REFERENCE) ?
                            FirebaseUtils.extractCoursesFromDataSnapshot(snap.child(FirebaseUtils.COURSE_REFERENCE))
                            : new ArrayList<>();

                    List<Pupil> pupils = snap.hasChild(FirebaseUtils.PUPIL_REFERENCE) ?
                            FirebaseUtils.extractPupilsFromDataSnapshot(snap.child(FirebaseUtils.PUPIL_REFERENCE))
                            : new ArrayList<>();

                    List<Location> locations = snap.hasChild(FirebaseUtils.LOCATION_REFERENCE) ?
                            FirebaseUtils.extractLocationsFromDataSnapshot(snap.child(FirebaseUtils.LOCATION_REFERENCE))
                            : new ArrayList<>();

                    List<Devoir> devoirs = snap.hasChild(FirebaseUtils.DEVOIR_REFERENCE) ?
                            FirebaseUtils.extractDevoirsFromDataSnapshot(snap.child(FirebaseUtils.DEVOIR_REFERENCE))
                            : new ArrayList<>();

                    snapshots.add(new Snapshot(date, courses, pupils, locations, devoirs));
                }

                boolean noSnapshots = snapshots.isEmpty();
                Log.e(TAG, "noSnapshots: " + noSnapshots);

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(SnapshotFragment.MyReceiver.SNAPSHOT_FRAGMENT);
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                broadcastIntent.putExtra(FIB_GET_SNAPSHOTS, (Serializable) snapshots);
                sendBroadcast(broadcastIntent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
