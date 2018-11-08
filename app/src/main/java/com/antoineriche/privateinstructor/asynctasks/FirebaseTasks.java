package com.antoineriche.privateinstructor.asynctasks;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.antoineriche.privateinstructor.beans.DatabaseItem;
import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.database.DevoirTable;
import com.antoineriche.privateinstructor.database.LocationTable;
import com.antoineriche.privateinstructor.database.MyDatabase;
import com.antoineriche.privateinstructor.database.PupilTable;
import com.antoineriche.privateinstructor.utils.FirebaseUtils;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class FirebaseTasks {

    private static final String TAG = FirebaseTasks.class.getSimpleName();

    public static class SynchronizeDatabases extends AsyncTask<List<DatabaseItem>, Double, Void> {

        private MyDatabase pMyDB;
        private SQLiteDatabase mDatabase;
        private DatabaseReference mFirebaseReference;
        private SynchronizeListener mListener;

        public SynchronizeDatabases(DatabaseReference pReference, MyDatabase pDatabase,
                                    SynchronizeListener pListener){
            this.mFirebaseReference = pReference;
            this.pMyDB = pDatabase;
            this.mListener = pListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDatabase = pMyDB.getReadableDatabase();
            mListener.startSynchronization(mFirebaseReference.getKey());
        }

        @Override
        protected void onProgressUpdate(Double... values) {
            super.onProgressUpdate(values);
            mListener.progress(mFirebaseReference.getKey(), values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mListener.itemsSuccessfullySynchronized(mFirebaseReference.getKey());
            mDatabase.close();
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(List<DatabaseItem>... lists) {
            List<DatabaseItem> remoteItems = lists[0];
            List<DatabaseItem> localItems = new ArrayList<>();

            if(FirebaseUtils.COURSE_REFERENCE.equals(this.mFirebaseReference.getKey())){
                localItems.addAll(CourseTable.getAllCourses(this.mDatabase));
            } else if(FirebaseUtils.PUPIL_REFERENCE.equals(this.mFirebaseReference.getKey())){
                localItems.addAll(PupilTable.getAllPupils(this.mDatabase));
            } else if(FirebaseUtils.LOCATION_REFERENCE.equals(this.mFirebaseReference.getKey())){
                localItems.addAll(LocationTable.getAllLocations(this.mDatabase));
            } else if(FirebaseUtils.DEVOIR_REFERENCE.equals(this.mFirebaseReference.getKey())){
                localItems.addAll(DevoirTable.getAllDevoirs(this.mDatabase));
            }

            // Deal with missing courses
            List<Long> itemsIdToAdd = getItemsIdToAddToFirebase(localItems, remoteItems);
            List<Long> itemsIdToRemove = getItemsIdToRemoveFromFirebase(localItems, remoteItems);
            List<Long> itemsIdToUpdate = getItemsIdToUpdateOnFirebase(localItems, remoteItems);

            double totalOperations = itemsIdToAdd.size() + itemsIdToRemove.size() + itemsIdToUpdate.size();
            double operationCount = 0;

            // Deal with new items to add
            if(!itemsIdToAdd.isEmpty()) {

                for(long itemId : itemsIdToAdd){
                    operationCount++;

                    DatabaseItem item = localItems.stream().filter(c -> c.getId() == itemId).findFirst().get();
                    double progress = (100 * operationCount) / totalOperations;

                    FirebaseUtils.saveItemInFirebase(item, mFirebaseReference,
                            o -> onProgressUpdate(progress),
                            e -> {
                                Log.e(TAG, "Fail to save item to Firebase");
                                mListener.failToSaveItemInFirebase(mFirebaseReference.getKey(), itemId, e.getMessage());
                            });
                }
            }

            // Deal with old items to remove
            if(!itemsIdToRemove.isEmpty()) {

                for(long itemId : itemsIdToRemove){
                    operationCount++;

                    String firebaseKey = String.format(Locale.FRANCE, "%04d", itemId);
                    double progress = (100 * operationCount) / totalOperations;

                    FirebaseUtils.removeItemFromFirebase(firebaseKey, mFirebaseReference,
                            (databaseError, databaseReference) -> {
                                if(databaseError != null){
                                    mListener.failToRemoveItemOfFirebase(mFirebaseReference.getKey(), itemId, databaseError.getMessage());
                                } else {
                                    onProgressUpdate(progress);
                                }
                            });
                }
            }

            // Deal with items to update
            if(!itemsIdToUpdate.isEmpty()) {

                for(long itemId : itemsIdToUpdate){
                    operationCount++;

                    DatabaseItem item = localItems.stream().filter(c -> c.getId() == itemId).findFirst().get();
                    double progress = (100 * operationCount) / totalOperations;

                    FirebaseUtils.saveItemInFirebase(item, mFirebaseReference,
                            o -> onProgressUpdate(progress),
                            e -> {
                                Log.e(TAG, "Fail to update item in Firebase");
                                mListener.failToUpdateItemInFirebase(mFirebaseReference.getKey(), itemId, e.getMessage());
                            });
                }
            }

            return null;
        }
    }

    private static List<Long> getItemsIdToAddToFirebase(List<DatabaseItem> localItems, List<DatabaseItem> remoteItems){
        List<Long> remote = remoteItems.stream().map(DatabaseItem::getId).collect(Collectors.toList());
        return localItems.stream().filter(c-> !remote.contains(c.getId())).map(DatabaseItem::getId).collect(Collectors.toList());
    }

    private static List<Long> getItemsIdToRemoveFromFirebase(List<DatabaseItem> localItems, List<DatabaseItem> remoteItems){
        List<Long> local = localItems.stream().map(DatabaseItem::getId).collect(Collectors.toList());
        return remoteItems.stream().filter(c-> !local.contains(c.getId())).map(DatabaseItem::getId).collect(Collectors.toList());
    }

    private static List<Long> getItemsIdToUpdateOnFirebase(List<DatabaseItem> localItems, List<DatabaseItem> remoteItems){
        List<Long> locals = localItems.stream().map(DatabaseItem::getId).collect(Collectors.toList());
        List<Long> remotes = remoteItems.stream().map(DatabaseItem::getId).collect(Collectors.toList());
        List<Long> both = locals.stream().filter(remotes::contains).collect(Collectors.toList());

        List<Long> toUpdate = new ArrayList<>();
        for(Long itemId : both){
            DatabaseItem local = localItems.stream().filter(c -> c.getId() == itemId).findFirst().get();
            DatabaseItem remote = remoteItems.stream().filter(c -> c.getId() == itemId).findFirst().get();
            if(!local.equals(remote)){ toUpdate.add(itemId); }
        }

        return toUpdate;
    }


    public interface SynchronizeListener {
        void startSynchronization(String pReferenceKey);
        void progress(String pReferenceKey, double percent);
        void failToSaveItemInFirebase(String pReferenceKey, long pItemId, String pError);
        void failToRemoveItemOfFirebase(String pReferenceKey, long pItemId, String pError);
        void failToUpdateItemInFirebase(String pReferenceKey, long pItemId, String pError);
        void itemsSuccessfullySynchronized(String pReferenceKey);
    }
}
