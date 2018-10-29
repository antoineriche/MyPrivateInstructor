package com.antoineriche.privateinstructor.services;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.database.MyDatabase;
import com.antoineriche.privateinstructor.utils.DateUtils;
import com.antoineriche.privateinstructor.utils.FirebaseUtils;
import com.antoineriche.privateinstructor.utils.PreferencesUtils;
import com.antoineriche.privateinstructor.utils.SnapshotFactory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class SnapshotService extends Service {

    String TAG = "SnapshotService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if(PreferencesUtils.getBooleanPreferences(this, getString(R.string.pref_enable_firebase_snapshot))){
            Log.e(TAG, "Snapshots are enabled");
            FirebaseUtils.getLastSnapshot(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String str = null;
                    for(DataSnapshot snap : dataSnapshot.getChildren()){
                        str = snap.getKey();
                    }
                    Date last = SnapshotFactory.extractDateFromSnapshot(str);
                    Log.e(TAG, "Last snapshot date is : " + last.toString());


                    long snapshotDelay = PreferencesUtils.getLongPreferences(getApplicationContext(),
                            getString(R.string.pref_firebase_snapshot_frequency));

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(last);

                    if (snapshotDelay == DateUtils.DAY){ calendar.add(Calendar.DAY_OF_YEAR, 1); }
                    else if (snapshotDelay == DateUtils.DAY * 7){ calendar.add(Calendar.DAY_OF_YEAR, 7); }
                    else if (snapshotDelay == DateUtils.DAY * 30){ calendar.add(Calendar.DAY_OF_YEAR, 30); }
                    else { stopSelf(); }

                    Log.e(TAG, "the new snapshot must be create on " + calendar.getTime().toString());

                    if(DateUtils.isPast(calendar.getTime())){
                        Log.e(TAG, "It's past, create snapshot now");
                        SQLiteDatabase db = new MyDatabase(getApplicationContext(), null).getReadableDatabase();
//                        FirebaseUtils.createSnapshotForCourses(CourseTable.getAllCourses(db), o -> stopSelf(), e -> {
//                            Log.e(TAG, "Error while create snapshot, bye bye");
//                            stopSelf();
//                        });
                    } else {
                        Log.e(TAG, "We got time bro'");
                        stopSelf();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "We got an error in the snapshot creation bro", databaseError.toException());
                    stopSelf();
                }
            });
        } else {
            Log.e(TAG, "Snapshot are not enabled, sorry bro'");
        }

        return START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) { return null; }
}
