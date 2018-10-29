package com.antoineriche.privateinstructor.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.activities.SnapshotFragment;
import com.antoineriche.privateinstructor.asynctasks.FirebaseTasks;
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.beans.DatabaseItem;
import com.antoineriche.privateinstructor.beans.Location;
import com.antoineriche.privateinstructor.beans.Pupil;
import com.antoineriche.privateinstructor.beans.Snapshot;
import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.database.LocationTable;
import com.antoineriche.privateinstructor.database.MyDatabase;
import com.antoineriche.privateinstructor.database.PupilTable;
import com.antoineriche.privateinstructor.utils.DateUtils;
import com.antoineriche.privateinstructor.utils.FirebaseUtils;
import com.antoineriche.privateinstructor.utils.PreferencesUtils;
import com.antoineriche.privateinstructor.utils.SnapshotFactory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FirebaseIntentService extends IntentService implements FirebaseTasks.SynchronizeListener {

    private static final String NAME = "FirebaseIntentService";
    private static final String TAG = NAME;

    public static final String FIB_TASKS = "FIB_TASKS";
    public static final String FIB_CHECK_SYNCHRONIZATION = "FIB_CHECK_SYNCHRONIZATION";
    public static final String FIB_CHECK_SNAPSHOT = "FIB_CHECK_SNAPSHOT";
    public static final String FIB_GET_LAST_SNAPSHOT_DATE = "FIB_GET_LAST_SNAPSHOT_DATE";
    public static final String FIB_GET_SNAPSHOTS = "FIB_GET_SNAPSHOTS";
    public static final String FIB_REPLACE_DATABASE = "FIB_REPLACE_DATABASE";
    public static final String FIB_NEW_DATABASE = "FIB_NEW_DATABASE";
    public static final String FIB_CREATE_SNAPSHOT = "FIB_CREATE_SNAPSHOT";


    public FirebaseIntentService() {
        super(NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "onHandleIntent: " + intent);
        boolean hasTasks = intent.hasExtra(FIB_TASKS);
        Log.e(TAG, "Intent has tasks: " + hasTasks);

        if(hasTasks){
            List<String> tasks = Arrays.asList(intent.getStringArrayExtra(FIB_TASKS));

            //First, make synchronization
            if (tasks.contains(FIB_CHECK_SYNCHRONIZATION)) { checkSynchronization(this, this); }
            if (tasks.contains(FIB_CHECK_SNAPSHOT)) { checkSnapshot(this); }
            if (tasks.contains(FIB_GET_LAST_SNAPSHOT_DATE)) { getLastSnapshot(); }
            if (tasks.contains(FIB_GET_SNAPSHOTS)) { getSnapshots(); }
            if (tasks.contains(FIB_REPLACE_DATABASE)) { askForReplacingDatabaseBySnapshot(intent.getStringExtra(FIB_NEW_DATABASE), intent.getBooleanExtra(FIB_CREATE_SNAPSHOT, false)); }
        }
    }

    private void askForReplacingDatabaseBySnapshot(String pSnapshotName, Boolean pCreateSnapshot) {
        Log.e(TAG, "ReplaceDatabase by: " + pSnapshotName + ", makeCopy: " + pCreateSnapshot);

        SQLiteDatabase database = new MyDatabase(getApplicationContext(), null).getWritableDatabase();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SnapshotFragment.MyReceiver.SNAPSHOT_FRAGMENT);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);

        if(pCreateSnapshot){
            FirebaseUtils.createSnapshot(database,
                    o -> {
                        Log.e(TAG, "Snapshot created.");
                        replaceDatabaseBySnapshot(pSnapshotName);
                    },
                    e -> Log.e(TAG, "Error while creating snapshot: " + e.getMessage())
            );
        } else {
            replaceDatabaseBySnapshot(pSnapshotName);
        }
    }

    private void replaceDatabaseBySnapshot(String pSnapshotName){
        SQLiteDatabase database = new MyDatabase(getApplicationContext(), null).getWritableDatabase();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SnapshotFragment.MyReceiver.SNAPSHOT_FRAGMENT);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);

        FirebaseUtils.getSnapshotWithKey(pSnapshotName, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Snapshot newSnap = FirebaseUtils.extractSnapshotFromDataSnapshot(dataSnapshot);

                CourseTable.clearTable(database);
                newSnap.getCourses().forEach(c -> CourseTable.insertCourse(database, c));

                PupilTable.clearTable(database);
                newSnap.getPupils().forEach(p -> PupilTable.insertPupil(database, p));

                LocationTable.clearTable(database);
                newSnap.getLocations().forEach(l -> LocationTable.insertLocation(database, l));

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

    private void checkSnapshot(Context pContext){
        boolean snapshotsEnabled = PreferencesUtils.getBooleanPreferences(pContext, getString(R.string.pref_enable_firebase_snapshot));
        Log.e(TAG, "Snapshots enabled: " + snapshotsEnabled);

        if(snapshotsEnabled){

            FirebaseUtils.getLastSnapshot(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String str = null;
                    for(DataSnapshot snap : dataSnapshot.getChildren()){ str = snap.getKey(); }
                    Date last = !TextUtils.isEmpty(str) ? SnapshotFactory.extractDateFromSnapshot(str) : new Date();
                    Log.e(TAG, "Last snapshot: " + last);

                    //FIXME use configuration
                    //long snapshotDelay = PreferencesUtils.getLongPreferences(getApplicationContext(),
                    //       getString(R.string.pref_firebase_snapshot_frequency));

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(last);
                    calendar.add(Calendar.DAY_OF_YEAR, 7);
                    Log.e(TAG, "Next snapshot: " + calendar.getTime());

                    boolean isTimeForSnapshot = DateUtils.isPast(calendar.getTime());
                    Log.e(TAG, "Is time for snapshot: " + isTimeForSnapshot);

                    if(isTimeForSnapshot){
                        SQLiteDatabase db = new MyDatabase(getApplicationContext(), null).getReadableDatabase();
                        FirebaseUtils.createSnapshot(db,
                                o -> Log.e(TAG, "Snapshot created."),
                                e -> Log.e(TAG, "Error while creating snapshot: " + e.getMessage())
                        );
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Error while saving snapshot.", databaseError.toException());
                }
            });
        }
    }

    private void checkSynchronization(Context pContext, FirebaseTasks.SynchronizeListener pListener){
        boolean synchronizationEnabled = PreferencesUtils.getBooleanPreferences(pContext, getString(R.string.pref_enable_firebase_synchronization));
        Log.e(TAG, "Synchronization enabled: " + synchronizationEnabled);

        if(synchronizationEnabled){

            FirebaseUtils.getDataFromFirebase(new FirebaseUtils.FirebaseListener() {
                @Override
                public void onRemoteCourses(List<Course> pCourses) {
                    List<DatabaseItem> items = new ArrayList<>();
                    items.addAll(pCourses);
                    //noinspection unchecked
                    new FirebaseTasks.SynchronizeDatabases(
                            FirebaseUtils.getCourseReference(),
                            new MyDatabase(pContext, null),
                            pListener).execute(items);
                }

                @Override
                public void onRemotePupils(List<Pupil> pPupils) {
                    List<DatabaseItem> items = new ArrayList<>();
                    items.addAll(pPupils);
                    //noinspection unchecked
                    new FirebaseTasks.SynchronizeDatabases(
                            FirebaseUtils.getPupilReference(),
                            new MyDatabase(pContext, null),
                            pListener).execute(items);
                }

                @Override
                public void onRemoteLocations(List<Location> pLocations) {
                    Log.e(TAG, "onRemoteLocations: " + pLocations.size());
                    List<DatabaseItem> items = new ArrayList<>();
                    items.addAll(pLocations);
                    //noinspection unchecked
                    new FirebaseTasks.SynchronizeDatabases(
                            FirebaseUtils.getLocationReference(),
                            new MyDatabase(pContext, null),
                            pListener).execute(items);
                }

                @Override
                public void cancelled(String pError) {
                    Log.e(TAG, "Error while retrieving data: " + pError);
                }
            });
        }
    }

    private void getLastSnapshot(){
        Log.e(TAG, "getLastSnapshot");

        FirebaseUtils.getLastSnapshot(new ValueEventListener() {
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

    private void getSnapshots(){
        Log.e(TAG, "getSnapshots");

        FirebaseUtils.getSnapshots(new ValueEventListener() {

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

                    snapshots.add(new Snapshot(date, courses, pupils, locations));
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


    @Override
    public void startSynchronization() {
        Log.e(TAG, "startSynchronization");
    }

    @Override
    public void progress(String pReferenceKey, double percent) { Log.e(TAG,  pReferenceKey + " x progress: " + percent); }

    @Override
    public void failToSaveItemInFirebase(long pItemId, String pError) {
        Log.e(TAG, "failToSaveItemInFirebase: " + pError);
    }

    @Override
    public void failToRemoveItemOfFirebase(long pItemId, String pError) {
        Log.e(TAG, "failToRemoveItemOfFirebase: " + pError);
    }

    @Override
    public void failToUpdateItemInFirebase(long pItemId, String pError) {
        Log.e(TAG, "failToUpdateItemInFirebase: " + pError);
    }

    @Override
    public void itemsSuccessfullySynchronized() { Log.e(TAG, "itemsSuccessfullySynchronized"); }
}
