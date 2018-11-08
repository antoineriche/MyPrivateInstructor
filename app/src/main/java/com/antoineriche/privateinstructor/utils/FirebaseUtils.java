package com.antoineriche.privateinstructor.utils;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.beans.DatabaseItem;
import com.antoineriche.privateinstructor.beans.Devoir;
import com.antoineriche.privateinstructor.beans.Location;
import com.antoineriche.privateinstructor.beans.Pupil;
import com.antoineriche.privateinstructor.beans.Snapshot;
import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.database.DevoirTable;
import com.antoineriche.privateinstructor.database.LocationTable;
import com.antoineriche.privateinstructor.database.PupilTable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FirebaseUtils {

    private final static String TAG = FirebaseUtils.class.getSimpleName();

    public final static String DATA_REFERENCE = "data";
    public final static String USER_INFO_REFERENCE = "user-info";
    public final static String SNAPSHOT_REFERENCE = "snapshots";
    public final static String COURSE_REFERENCE = "courses";
    public final static String DEVOIR_REFERENCE = "devoirs";
    public final static String PUPIL_REFERENCE = "pupils";
    public final static String LOCATION_REFERENCE = "locations";


    public final static String USER_LOCATION = "location";

    private static FirebaseDatabase getDatabase() {
        return FirebaseDatabase.getInstance();
    }

    private static DatabaseReference getUserReference(String pUserUid) {
        return getDatabase().getReference(pUserUid);
    }

    private static DatabaseReference getUserInfoReference(String pUserUid){
        return getUserReference(pUserUid).child(USER_INFO_REFERENCE);
    }

    private static DatabaseReference getDataReference(String pUserUid){
        return getUserReference(pUserUid).child(DATA_REFERENCE);
    }

    public static DatabaseReference getCourseReference(String pUserUid){
        return getDataReference(pUserUid).child(COURSE_REFERENCE);
    }

    public static DatabaseReference getPupilReference(String pUserUid){
        return getDataReference(pUserUid).child(PUPIL_REFERENCE);
    }

    public static DatabaseReference getLocationReference(String pUserUid){
        return getDataReference(pUserUid).child(LOCATION_REFERENCE);
    }

    public static DatabaseReference getDevoirReference(String pUserUid){
        return getDataReference(pUserUid).child(DEVOIR_REFERENCE);
    }

    private static DatabaseReference getSnapshotReference(String pUserUid){
        return getUserReference(pUserUid).child(SNAPSHOT_REFERENCE);
    }

    private static void updateUserInfo(String pUserUid, String infoKey, Object infoValue, OnSuccessListener pSuccessListener, OnFailureListener pFailureListener){
        getUserInfoReference(pUserUid).child(infoKey).setValue(infoValue)
                .addOnSuccessListener(pSuccessListener)
                .addOnFailureListener(pFailureListener);
    }

    public static void updateUserLocation(String pUserUid, String locationUuid, OnSuccessListener pSuccessListener, OnFailureListener pFailureListener){
        updateUserInfo(pUserUid, USER_LOCATION, locationUuid, pSuccessListener, pFailureListener);
    }

    public static void saveItemInFirebase(DatabaseItem item, DatabaseReference pReference, OnSuccessListener pSuccessListener, OnFailureListener pFailureListener){
        String pKey = String.format(Locale.FRANCE, "%04d", item.getId());
        pReference.child(pKey).setValue(item.toMap()).addOnSuccessListener(pSuccessListener)
                .addOnFailureListener(pFailureListener);
    }

    public static void createSnapshot(SQLiteDatabase pDatabase, String pUserUid, SaveSnapshotListener pListener){

        String snapshotName = SnapshotFactory.createSnapshotName();

        DatabaseReference reference = getSnapshotReference(pUserUid).child(snapshotName);

        Map<String, Map<String, Map<String, Object>>> root = new HashMap<>();

        Map<String, Map<String, Object>> courses = new HashMap<>();
        List<Course> lCourses = CourseTable.getAllCourses(pDatabase);
        lCourses.forEach(c -> courses.put(c.generateDatabaseId(), c.toMap()));
        root.put(COURSE_REFERENCE, courses);

        Map<String, Map<String, Object>> devoirs = new HashMap<>();
        List<Devoir> lDevoirs = DevoirTable.getAllDevoirs(pDatabase);
        lDevoirs.forEach(c -> devoirs.put(c.generateDatabaseId(), c.toMap()));
        root.put(DEVOIR_REFERENCE, devoirs);

        Map<String, Map<String, Object>> pupils = new HashMap<>();
        List<Pupil> lPupils = PupilTable.getAllPupils(pDatabase);
        lPupils.forEach(p -> pupils.put(p.generateDatabaseId(), p.toMap()));
        root.put(PUPIL_REFERENCE, pupils);

        Map<String, Map<String, Object>> locations = new HashMap<>();
        List<Location> lLocations = LocationTable.getAllLocations(pDatabase);
        lLocations.forEach(l -> locations.put(l.generateDatabaseId(), l.toMap()));
        root.put(LOCATION_REFERENCE, locations);

        reference.setValue(root)
                .addOnSuccessListener(aVoid -> pListener.snapshotSaved(new Snapshot(
                        SnapshotFactory.extractDateFromSnapshot(snapshotName),
                        lCourses, lPupils, lLocations, lDevoirs)))
                .addOnFailureListener(e -> pListener.snapshotSavingFailed(e.getMessage()));
    }

    public static void removeItemFromFirebase(String pFirebaseKey, DatabaseReference pReference, DatabaseReference.CompletionListener pListener){
        pReference.child(pFirebaseKey).removeValue(pListener);
    }

    public static void getSnapshots(String pUserUid, ValueEventListener pValueEventListener){
        getSnapshotReference(pUserUid).addListenerForSingleValueEvent(pValueEventListener);
    }

    public static void getSnapshotWithKey(String pUserUid, String pSnapshotKey, ValueEventListener pValueEventListener){
        getSnapshotReference(pUserUid).child(pSnapshotKey).addListenerForSingleValueEvent(pValueEventListener);
    }

    public static void getLastSnapshot(String pUserUid, ValueEventListener pValueEventListener){
        getSnapshotReference(pUserUid).orderByKey().limitToLast(1).addListenerForSingleValueEvent(pValueEventListener);
    }

    public static Snapshot extractSnapshotFromDataSnapshot(DataSnapshot pDataSnapshot){
        return new Snapshot(SnapshotFactory.extractDateFromSnapshot(pDataSnapshot.getKey()),
                extractCoursesFromDataSnapshot(pDataSnapshot.child(COURSE_REFERENCE)),
                extractPupilsFromDataSnapshot(pDataSnapshot.child(PUPIL_REFERENCE)),
                extractLocationsFromDataSnapshot(pDataSnapshot.child(LOCATION_REFERENCE)),
                extractDevoirsFromDataSnapshot(pDataSnapshot.child(DEVOIR_REFERENCE)));
    }

    public static List<Course> extractCoursesFromDataSnapshot(DataSnapshot pDataSnapshot){
        List<Course> list = new ArrayList<>();
        for (DataSnapshot snap : pDataSnapshot.getChildren()){
            Course course;
            try {
                course = snap.getValue(Course.class);
            } catch (Exception e){
                Log.e(TAG, "Error while retrieving course, this one is malformed, only keep id");
                course = new Course(Long.valueOf(snap.getKey()));
            }
            list.add(course);
        }
        return list;
    }

    public static List<Pupil> extractPupilsFromDataSnapshot(DataSnapshot pDataSnapshot){
        List<Pupil> list = new ArrayList<>();
        for (DataSnapshot snap : pDataSnapshot.getChildren()){
            Pupil pupil;
            try {
                pupil = snap.getValue(Pupil.class);
            } catch (Exception e){
                Log.e(TAG, "Error while retrieving pupil, this one is malformed, only keep id");
                pupil = new Pupil(Long.valueOf(snap.getKey()));
            }
            list.add(pupil);
        }
        return list;
    }

    public static List<Location> extractLocationsFromDataSnapshot(DataSnapshot pDataSnapshot){
        List<Location> list = new ArrayList<>();
        for (DataSnapshot snap : pDataSnapshot.getChildren()){
            Location location;
            try {
                location = snap.getValue(Location.class);
            } catch (Exception e){
                Log.e(TAG, "Error while retrieving location, this one is malformed, only keep id");
                location = new Location(Long.valueOf(snap.getKey()));
            }
            list.add(location);
        }
        return list;
    }

    public static List<Devoir> extractDevoirsFromDataSnapshot(DataSnapshot pDataSnapshot){
        List<Devoir> list = new ArrayList<>();
        for (DataSnapshot snap : pDataSnapshot.getChildren()){
            Devoir devoir;
            try {
                devoir = snap.getValue(Devoir.class);
            } catch (Exception e){
                Log.e(TAG, "Error while retrieving location, this one is malformed, only keep id");
                devoir = new Devoir(Long.valueOf(snap.getKey()));
            }
            list.add(devoir);
        }
        return list;
    }


    public static void getUserDataFromFirebase(String pUserUid, FirebaseListener pListener){
        getDataReference(pUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                pListener.onRemoteItems(dataSnapshot.hasChild(COURSE_REFERENCE) ?
                        new ArrayList<>(extractCoursesFromDataSnapshot(dataSnapshot.child(COURSE_REFERENCE)))
                        : new ArrayList<>(), getCourseReference(pUserUid));

                pListener.onRemoteItems(dataSnapshot.hasChild(PUPIL_REFERENCE) ?
                        new ArrayList<>(extractPupilsFromDataSnapshot(dataSnapshot.child(PUPIL_REFERENCE)))
                        : new ArrayList<>(), getPupilReference(pUserUid));

                pListener.onRemoteItems(dataSnapshot.hasChild(LOCATION_REFERENCE) ?
                        new ArrayList<>(extractLocationsFromDataSnapshot(dataSnapshot.child(LOCATION_REFERENCE)))
                        : new ArrayList<>(), getLocationReference(pUserUid));

                pListener.onRemoteItems(dataSnapshot.hasChild(DEVOIR_REFERENCE) ?
                        new ArrayList<>(extractDevoirsFromDataSnapshot(dataSnapshot.child(DEVOIR_REFERENCE)))
                        : new ArrayList<>(), getDevoirReference(pUserUid));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pListener.cancelled(databaseError.getMessage());
            }
        });
    }

    public interface FirebaseListener {
        void onRemoteItems(List<DatabaseItem> pRemoteItems, DatabaseReference pDatabaseReference);
        void cancelled(String pError);
    }

    public interface SaveSnapshotListener {
        void snapshotSaved(Snapshot newSnapshot);
        void snapshotSavingFailed(String pError);
    }
}
