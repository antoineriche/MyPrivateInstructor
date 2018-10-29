package com.antoineriche.privateinstructor.services;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.antoineriche.privateinstructor.asynctasks.CourseTasks;
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.beans.Pupil;
import com.antoineriche.privateinstructor.beans.Snapshot;
import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.database.MyDatabase;
import com.antoineriche.privateinstructor.utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FirebaseService extends Service {

    private static String TAG = "FirebaseService";

    private static SQLiteDatabase mDatabase;

    public static final String MSG_EXTRA_DATA = "firebase-service";
    public static final int MSG_SYNC_CHECK_STATE = 40;
    public static final int MSG_SYNC_CHECK_START = 41;
    public static final int MSG_SYNC_CHECK_END = 42;
    public static final int MSG_SYNC_CHECK_COURSE_TO_ADD_COUNT = 43;
    public static final int MSG_SYNC_CHECK_COURSE_TO_REMOVE_COUNT = 44;
    public static final int MSG_SYNC_CHECK_COURSE_TO_UPDATE_COUNT = 45;
    public static final int MSG_SYNCHRONIZATION_START = 50;
    public static final int MSG_SYNCHRONIZATION_SUCCESS = 51;
    public static final int MSG_SYNCHRONIZATION_ERROR = 52;
    public static final int MSG_SNAPSHOT_START = 60;
    public static final int MSG_SNAPSHOT_SUCCESS = 61;
    public static final int MSG_SNAPSHOT_ERROR = 62;

    static class FirebaseServiceHandler extends Handler {

        private final WeakReference<FirebaseService> mService;

        FirebaseServiceHandler(FirebaseService service) {
            mService = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SYNC_CHECK_STATE:
                    mService.get().checkSyncState(msg.replyTo);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public class LocalBinder extends Binder {
        public FirebaseService getService() {
            return FirebaseService.this;
        }

        public Messenger getMessenger() {
            return mMessenger;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    final Messenger mMessenger = new Messenger(new FirebaseServiceHandler(this));

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase = new MyDatabase(this, null).getWritableDatabase();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDatabase.isOpen()) {
            mDatabase.close();
        }
    }

    public void checkSyncState(Messenger pMessenger) {

//        FirebaseUtils.getCoursesFromFirebase(new FirebaseUtils.FirebaseListener() {
//            @Override
//            public void onRemoteCourses(List<Course> pCourses) {
//
//            }
//
//            @Override
//            public void onRemotePupils(List<Pupil> pPupils) {
//
//            }
//
//            @Override
//            public void onRemoteData(List items) {
//                //noinspection unchecked
//                new CourseTasks.CheckingSynchronizationState(new MyDatabase(getBaseContext(), null), new CourseTasks.CheckingCourseListener() {
//                    @Override
//                    public void startComparing() {
//                        Message message = Message.obtain(null, MSG_SYNC_CHECK_START);
//                        sendMessage(pMessenger, message);
//                    }
//
//                    @Override
//                    public void endComparing() {
//                        Message message = Message.obtain(null, MSG_SYNC_CHECK_END);
//                        sendMessage(pMessenger, message);
//                    }
//
//                    @Override
//                    public void isFirebaseUpToDate(boolean isUpToDate) {
//                        Message message = Message.obtain(null, MSG_SYNC_CHECK_STATE);
//                        Bundle bb = new Bundle();
//                        bb.putBoolean(MSG_EXTRA_DATA, isUpToDate);
//                        message.setData(bb);
//                        sendMessage(pMessenger, message);
//                    }
//
//                    @Override
//                    public void courseToAddCount(int count) {
//                        Message message = Message.obtain(null, MSG_SYNC_CHECK_COURSE_TO_ADD_COUNT);
//                        Bundle bb = new Bundle();
//                        bb.putInt(MSG_EXTRA_DATA, count);
//                        message.setData(bb);
//                        sendMessage(pMessenger, message);
//                    }
//
//                    @Override
//                    public void courseToRemove(int count) {
//                        Message message = Message.obtain(null, MSG_SYNC_CHECK_COURSE_TO_REMOVE_COUNT);
//                        Bundle bb = new Bundle();
//                        bb.putInt(MSG_EXTRA_DATA, count);
//                        message.setData(bb);
//                        sendMessage(pMessenger, message);
//                    }
//
//                    @Override
//                    public void courseToUpdate(int count) {
//                        Message message = Message.obtain(null, MSG_SYNC_CHECK_COURSE_TO_UPDATE_COUNT);
//                        Bundle bb = new Bundle();
//                        bb.putInt(MSG_EXTRA_DATA, count);
//                        message.setData(bb);
//                        sendMessage(pMessenger, message);
//                    }
//                }).execute((List<Course>) items);
//            }
//
//            @Override
//            public void cancelled(String pError) {
//            }
//        });
    }

    public void synchronizeRemoteDataBase(Messenger pMessenger) {
//        FirebaseUtils.getCoursesFromFirebase(new FirebaseUtils.FirebaseListener() {
//            @Override
//            public void onRemoteCourses(List<Course> pCourses) {
//
//            }
//
//            @Override
//            public void onRemotePupils(List<Pupil> pPupils) {
//
//            }
//
//            @Override
//            public void onRemoteData(List items) {
//                //noinspection unchecked
//                new CourseTasks.SynchronizeCourses(new MyDatabase(getBaseContext(), null), new CourseTasks.SynchronizeCourseListener() {
//                    @Override
//                    public void startCoursesSynchronization() {
//                        Message message = Message.obtain(null, MSG_SYNCHRONIZATION_START);
//                        sendMessage(pMessenger, message);
//                    }
//
//                    @Override
//                    public void progress(double percent) {
//                        Log.e(TAG, "Progress: " + percent);
//                    }
//
//                    @Override
//                    public void failToSaveCourseInFirebase(long pCourseId, String pError) {
//                        Log.e(TAG, "Unable to save " + pCourseId + " to Firebase: " + pError);
//                        Message message = Message.obtain(null, MSG_SYNCHRONIZATION_ERROR);
//                        Bundle bb = new Bundle();
//                        bb.putString(MSG_EXTRA_DATA,
//                                String.format(Locale.FRANCE, "Unable to save %d to Firebase %s", pCourseId, pError));
//                        message.setData(bb);
//                        sendMessage(pMessenger, message);
//                    }
//
//                    @Override
//                    public void failToRemoveCourseOfFirebase(long pCourseId, String pError) {
//                        Log.e(TAG, "Unable to remove " + pCourseId + " from Firebase: " + pError);
//                        Message message = Message.obtain(null, MSG_SYNCHRONIZATION_ERROR);
//                        Bundle bb = new Bundle();
//                        bb.putString(MSG_EXTRA_DATA,
//                                String.format(Locale.FRANCE, "Unable to remove %d from Firebase %s", pCourseId, pError));
//                        message.setData(bb);
//                        sendMessage(pMessenger, message);
//                    }
//
//                    @Override
//                    public void failToUpdateCourseInFirebase(long pCourseId, String pError) {
//                        Log.e(TAG, "Unable to update " + pCourseId + " in Firebase: " + pError);
//                        Message message = Message.obtain(null, MSG_SYNCHRONIZATION_ERROR);
//                        Bundle bb = new Bundle();
//                        bb.putString(MSG_EXTRA_DATA,
//                                String.format(Locale.FRANCE, "Unable to update %d in Firebase %s", pCourseId, pError));
//                        message.setData(bb);
//                        sendMessage(pMessenger, message);
//                    }
//
//                    @Override
//                    public void coursesSuccessfullySynchronized() {
//                        Message message = Message.obtain(null, MSG_SYNCHRONIZATION_SUCCESS);
//                        sendMessage(pMessenger, message);
//                    }
//                }).execute((List<Course>) items);
//            }
//
//            @Override
//            public void cancelled(String pError) {
//            }
//        });
    }

    public void createSnapshot(Messenger pMessenger){
        new CourseTasks.SnapshotCourses(new MyDatabase(getBaseContext(), null), new CourseTasks.SnapshotListener() {
            @Override
            public void startSnapshotTask() {
                Log.e(TAG, "Snapshot starts");
                Message message = Message.obtain(null, MSG_SNAPSHOT_START);
                sendMessage(pMessenger, message);
            }

            @Override
            public void snapshotSuccess(String pSnapshotName) {
                Log.e(TAG, "Snapshot successfully created " + pSnapshotName);
                Message message = Message.obtain(null, MSG_SNAPSHOT_SUCCESS);
                Bundle bb = new Bundle();
                bb.putString(MSG_EXTRA_DATA,
                        String.format(Locale.FRANCE, "Snapshot %s successfully created", pSnapshotName));
                message.setData(bb);
                sendMessage(pMessenger, message);
            }

            @Override
            public void snapshotError(String pError) {
                Log.e(TAG, "Snapshot error " + pError);
                Message message = Message.obtain(null, MSG_SNAPSHOT_ERROR);
                Bundle bb = new Bundle();
                bb.putString(MSG_EXTRA_DATA,
                        String.format(Locale.FRANCE, "Error while saving snapshot\n%s", pError));
                message.setData(bb);
                sendMessage(pMessenger, message);
            }
        }).execute();
    }

    public void retrieveOldSnapshot(ValueEventListener pValueEventListener){
        FirebaseUtils.getSnapshots(pValueEventListener);
    }

    public void replaceCurrentDBWithSnapshot(DataSnapshot dataSnapshot){
        List<Course> courses = new ArrayList<>();
        String snapName = dataSnapshot.getKey();
        Log.e(TAG, "We are going to use: " + snapName);
        for(DataSnapshot snap : dataSnapshot.getChildren()){
            courses.add(snap.getValue(Course.class));
        }
        Log.e(TAG, snapName + " contains " + courses.size() + " courses");
        CourseTable.clearTable(mDatabase);
        for(Course course : courses){
            CourseTable.insertCourse(mDatabase, course);
        }
        Log.e(TAG, "Databases have been updated");
    }

    public void replaceCurrentDBWithSnapshot(Snapshot pSnapshot){
        List<Course> courses = new ArrayList<>();
        CourseTable.clearTable(mDatabase);
        for(Course course : courses){
            CourseTable.insertCourse(mDatabase, course);
        }
        Log.e(TAG, "Databases have been updated");
    }

    private void sendMessage(Messenger pMessenger, Message pMessage){
        try {
            pMessenger.send(pMessage);
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to send message", e);
        }
    }
}
