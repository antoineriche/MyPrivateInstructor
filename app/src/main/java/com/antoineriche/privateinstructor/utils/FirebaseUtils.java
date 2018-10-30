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
    public final static String COURSE_REFERENCE = "courses";
    public final static String DEVOIR_REFERENCE = "devoirs";
    public final static String PUPIL_REFERENCE = "pupils";
    public final static String LOCATION_REFERENCE = "locations";
    public final static String SNAPSHOT_REFERENCE = "snapshots";

    private static FirebaseDatabase getDatabase() {
        return FirebaseDatabase.getInstance();
    }

    private static DatabaseReference getDataReference(){
        return getDatabase().getReference(DATA_REFERENCE);
    }

    public static DatabaseReference getCourseReference(){
        return getDataReference().child(COURSE_REFERENCE);
    }

    public static DatabaseReference getPupilReference(){
        return getDataReference().child(PUPIL_REFERENCE);
    }

    public static DatabaseReference getLocationReference(){
        return getDataReference().child(LOCATION_REFERENCE);
    }

    public static DatabaseReference getDevoirReference(){
        return getDataReference().child(DEVOIR_REFERENCE);
    }

    private static DatabaseReference getSnapshotReference(){
        return getDatabase().getReference(SNAPSHOT_REFERENCE);
    }

    public static void saveCourseInFirebase(Course course, OnSuccessListener pSuccessListener, OnFailureListener pFailureListener){
        saveItemInFirebase(course, getCourseReference(), pSuccessListener, pFailureListener);
    }

    public static void saveItemInFirebase(DatabaseItem item, DatabaseReference pReference, OnSuccessListener pSuccessListener, OnFailureListener pFailureListener){
        String pKey = String.format(Locale.FRANCE, "%04d", item.getId());
        pReference.child(pKey).setValue(item.toMap())
                .addOnSuccessListener(pSuccessListener).addOnFailureListener(pFailureListener);
    }

    public static void createSnapshot(SQLiteDatabase pDatabase, OnSuccessListener pSuccessListener, OnFailureListener pFailureListener){

        String snapshotName = SnapshotFactory.createSnapshotName();

        DatabaseReference reference = getSnapshotReference().child(snapshotName);

        Map<String, Map<String, Map<String, Object>>> root = new HashMap<>();

        Map<String, Map<String, Object>> courses = new HashMap<>();
        CourseTable.getAllCourses(pDatabase).forEach(c -> courses.put(c.generateDatabaseId(), c.toMap()));
        root.put(COURSE_REFERENCE, courses);

        Map<String, Map<String, Object>> devoirs = new HashMap<>();
        DevoirTable.getAllDevoirs(pDatabase).forEach(c -> devoirs.put(c.generateDatabaseId(), c.toMap()));
        root.put(DEVOIR_REFERENCE, devoirs);

        Map<String, Map<String, Object>> pupils = new HashMap<>();
        PupilTable.getAllPupils(pDatabase).forEach(p -> pupils.put(p.generateDatabaseId(), p.toMap()));
        root.put(PUPIL_REFERENCE, pupils);

        Map<String, Map<String, Object>> locations = new HashMap<>();
        LocationTable.getAllLocations(pDatabase).forEach(l -> locations.put(l.generateDatabaseId(), l.toMap()));
        root.put(LOCATION_REFERENCE, locations);

        reference.setValue(root).addOnSuccessListener(pSuccessListener).addOnFailureListener(pFailureListener);
    }

    public static void removeCourseFromFirebase(String pFirebaseKey, DatabaseReference.CompletionListener pListener){
        getCourseReference().child(pFirebaseKey).removeValue(pListener);
    }

    public static void removeItemFromFirebase(String pFirebaseKey, DatabaseReference pReference, DatabaseReference.CompletionListener pListener){
        pReference.child(pFirebaseKey).removeValue(pListener);
    }

    public static void getSnapshots(ValueEventListener pValueEventListener){
        getSnapshotReference().addListenerForSingleValueEvent(pValueEventListener);
    }

    public static void getSnapshotWithKey(String pSnapshotKey, ValueEventListener pValueEventListener){
        getSnapshotReference().child(pSnapshotKey).addListenerForSingleValueEvent(pValueEventListener);
    }

    public static void getLastSnapshot(ValueEventListener pValueEventListener){
        getSnapshotReference().orderByKey().limitToLast(1).addListenerForSingleValueEvent(pValueEventListener);
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

    public static void getDataFromFirebase(FirebaseListener pListener){
        getDataReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                pListener.onRemoteCourses(dataSnapshot.hasChild(COURSE_REFERENCE) ?
                        extractCoursesFromDataSnapshot(dataSnapshot.child(COURSE_REFERENCE))
                        : new ArrayList<>());

                pListener.onRemotePupils(dataSnapshot.hasChild(PUPIL_REFERENCE) ?
                        extractPupilsFromDataSnapshot(dataSnapshot.child(PUPIL_REFERENCE))
                        : new ArrayList<>());

                pListener.onRemoteLocations(dataSnapshot.hasChild(LOCATION_REFERENCE) ?
                        extractLocationsFromDataSnapshot(dataSnapshot.child(LOCATION_REFERENCE))
                        : new ArrayList<>());

                pListener.onRemoteDevoirs(dataSnapshot.hasChild(DEVOIR_REFERENCE) ?
                        extractDevoirsFromDataSnapshot(dataSnapshot.child(DEVOIR_REFERENCE))
                        : new ArrayList<>());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pListener.cancelled(databaseError.getMessage());
            }
        });
    }

    public interface FirebaseListener{
        void onRemoteCourses(List<Course> pCourses);
        void onRemotePupils(List<Pupil> pPupils);
        void onRemoteLocations(List<Location> pLocations);
        void onRemoteDevoirs(List<Devoir> pDevoirs);
        void cancelled(String pError);
    }
}
