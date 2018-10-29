package com.antoineriche.privateinstructor.asynctasks;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.database.MyDatabase;
import com.antoineriche.privateinstructor.utils.FirebaseUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CourseTasks {

    public static class CheckingSynchronizationState extends AsyncTask<List<Course>, Void, Boolean> {

        String TAG = "CheckingSynchronizationState";
        private CheckingCourseListener listener;
        private SQLiteDatabase database;
        private MyDatabase pMyDB;

        public CheckingSynchronizationState(MyDatabase pDatabase, CheckingCourseListener pListener){
            listener = pListener;
            pMyDB = pDatabase;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            database = pMyDB.getReadableDatabase();
            listener.startComparing();
        }

        @SafeVarargs
        @Override
        protected final Boolean doInBackground(List<Course>... lists) {
            List<Course> remoteCourses = lists[0];
            List<Course> localCourses = CourseTable.getAllCourses(database);

            int toAdd = getCoursesIdToAddToFirebase(localCourses, remoteCourses).size();
            int toRemove = getCoursesIdToRemoveFromFirebase(localCourses, remoteCourses).size();
            int toUpdate = getCoursesIdToUpdateOnFirebase(localCourses, remoteCourses).size();

            listener.courseToAddCount(toAdd);
            listener.courseToRemove(toRemove);
            listener.courseToUpdate(toUpdate);

            return toAdd ==  0 && toRemove == 0 && toUpdate == 0;
        }

        @Override
        protected void onPostExecute(Boolean uptodate) {
            super.onPostExecute(uptodate);
            listener.endComparing();
            listener.isFirebaseUpToDate(uptodate);
            database.close();
        }
    }

    public static class SynchronizeCourses extends AsyncTask<List<Course>, Double, Void> {

        String TAG = "SynchronizeCourses";
        private SynchronizeCourseListener listener;
        private SQLiteDatabase database;
        private MyDatabase pMyDB;

        public SynchronizeCourses(MyDatabase pDatabase, SynchronizeCourseListener pListener){
            listener = pListener;
            pMyDB = pDatabase;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            database = pMyDB.getReadableDatabase();
            listener.startCoursesSynchronization();
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(List<Course>... lists) {
            List<Course> remoteCourses = lists[0];
            List<Course> localCourses = CourseTable.getAllCourses(database);

            // Deal with missing courses
            List<Long> coursesIdToAdd = getCoursesIdToAddToFirebase(localCourses, remoteCourses);
            List<Long> coursesIdToRemove = getCoursesIdToRemoveFromFirebase(localCourses, remoteCourses);
            List<Long> coursesIdToUpdate = getCoursesIdToUpdateOnFirebase(localCourses, remoteCourses);

            double totalOperations = coursesIdToAdd.size() + coursesIdToRemove.size() + coursesIdToUpdate.size();
            double operationCount = 0;

            if(!coursesIdToAdd.isEmpty()) {

                for(long courseId : coursesIdToAdd){
                    operationCount++;

                    Course course = localCourses.stream().filter(c -> c.getId() == courseId).findFirst().get();
                    double progress = (100 * operationCount) / totalOperations;

                    FirebaseUtils.saveCourseInFirebase(course, o -> onProgressUpdate(progress), e -> {
                        Log.e(TAG, "Fail to save course to Firebase");
                        listener.failToSaveCourseInFirebase(courseId, e.getMessage());
                    });
                }
            } else {
                Log.e(TAG, "All local courses are already in Firebase.");
            }

            // Deal with old courses to remove
            if(!coursesIdToRemove.isEmpty()) {

                for(long courseId : coursesIdToRemove){
                    operationCount++;

                    String firebaseKey = String.format(Locale.FRANCE, "%04d", courseId);
                    Log.e(TAG, "Old courseId: " + firebaseKey);
                    double progress = (100 * operationCount) / totalOperations;

                    FirebaseUtils.removeCourseFromFirebase(firebaseKey, (databaseError, databaseReference) -> {
                        if(databaseError != null){
                            listener.failToRemoveCourseOfFirebase(courseId, databaseError.getMessage());
                        } else {
                            onProgressUpdate(progress);
                        }
                    });
                }
            } else {
                Log.e(TAG, "Firebase does not contains any old course.");
            }

            // Deal with courses to update
            if(!coursesIdToUpdate.isEmpty()) {

                for(long courseId : coursesIdToUpdate){
                    operationCount++;

                    Course course = localCourses.stream().filter(c -> c.getId() == courseId).findFirst().get();
                    double progress = (100 * operationCount) / totalOperations;

                    FirebaseUtils.saveCourseInFirebase(course, o -> onProgressUpdate(progress), e -> {
                        Log.e(TAG, "Fail to update course in Firebase");
                        listener.failToUpdateCourseInFirebase(courseId, e.getMessage());
                    });
                }
            } else {
                Log.e(TAG, "Firebase does not contains any old course to update.");
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Double... values) {
            super.onProgressUpdate(values);
            listener.progress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            listener.coursesSuccessfullySynchronized();
            database.close();
        }
    }

    public static class SnapshotCourses extends AsyncTask<Void, Void, String> {

        String TAG = "SnapshotCourses";
        private SnapshotListener listener;
        private SQLiteDatabase database;
        private MyDatabase pMyDB;

        public SnapshotCourses(MyDatabase pDatabase, SnapshotListener pListener){
            listener = pListener;
            pMyDB = pDatabase;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            database = pMyDB.getReadableDatabase();
            listener.startSnapshotTask();
        }

        @Override
        protected String doInBackground(Void... voids) {
            List<Course> localCourses = CourseTable.getAllCourses(database);

            String snapshotName = String.format(Locale.FRANCE, "snapshot-courses-%s",
                    new SimpleDateFormat("yyyyMMddHHmmss", Locale.FRANCE).format(new Date()));

//            FirebaseUtils.createSnapshot(localCourses, snapshotName,
//                    o -> Log.e(TAG, "Successfully added"),
//                    e -> {
//                        Log.e(TAG, "Error while saving snapshot");
//                        listener.snapshotError(e.getMessage());
//                    });
            return snapshotName;
        }

        @Override
        protected void onPostExecute(String aString) {
            super.onPostExecute(aString);
            listener.snapshotSuccess(aString);
            database.close();
        }
    }





    private static List<Long> getCoursesIdToAddToFirebase(List<Course> localCourses, List<Course> remoteCourses){
        List<Long> remote = remoteCourses.stream().map(Course::getId).collect(Collectors.toList());
        return localCourses.stream().filter(c-> !remote.contains(c.getId())).map(Course::getId).collect(Collectors.toList());
    }

    private static List<Long> getCoursesIdToRemoveFromFirebase(List<Course> localCourses, List<Course> remoteCourses){
        List<Long> local = localCourses.stream().map(Course::getId).collect(Collectors.toList());
        return remoteCourses.stream().filter(c-> !local.contains(c.getId())).map(Course::getId).collect(Collectors.toList());
    }

    private static List<Long> getCoursesIdToUpdateOnFirebase(List<Course> localCourses, List<Course> remoteCourses){
        List<Long> local = localCourses.stream().map(Course::getId).collect(Collectors.toList());
        List<Long> remote = remoteCourses.stream().map(Course::getId).collect(Collectors.toList());
        List<Long> both = local.stream().filter(remote::contains).collect(Collectors.toList());

        List<Long> toUpdate = new ArrayList<>();
        for(Long courseId : both){
            Course localCourse = localCourses.stream().filter(c -> c.getId() == courseId).findFirst().get();
            Course remoteCourse = remoteCourses.stream().filter(c -> c.getId() == courseId).findFirst().get();
            if(!localCourse.equals(remoteCourse)){
                toUpdate.add(courseId);
            }
        }

        return toUpdate;
    }


    public interface SynchronizeCourseListener {
        void startCoursesSynchronization();
        void progress(double percent);
        void failToSaveCourseInFirebase(long pCourseId, String pError);
        void failToRemoveCourseOfFirebase(long pCourseId, String pError);
        void failToUpdateCourseInFirebase(long pCourseId, String pError);
        void coursesSuccessfullySynchronized();
    }

    public interface CheckingCourseListener {
        void startComparing();
        void endComparing();
        void isFirebaseUpToDate(boolean isUpToDate);
        void courseToAddCount(int count);
        void courseToRemove(int count);
        void courseToUpdate(int count);
    }

    public interface SnapshotListener {
        void startSnapshotTask();
        void snapshotSuccess(String pSnapshotName);
        void snapshotError(String pError);
    }
}
