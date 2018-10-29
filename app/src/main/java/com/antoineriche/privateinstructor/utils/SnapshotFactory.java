package com.antoineriche.privateinstructor.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SnapshotFactory {

    private static final String TAG = "SnapshotFactory";

    private static final SimpleDateFormat mSDF = new SimpleDateFormat("yyyyMMddHHmmss", Locale.FRANCE);

    public static Date extractDateFromSnapshot(String pSnapshotName){
        String str = pSnapshotName.substring(pSnapshotName.lastIndexOf("-")+1);
        try {
            return mSDF.parse(str);
        } catch (ParseException e) {
            Log.e(TAG, "Parsing error", e);
            return new Date(0);
        }
    }

    public static String createSnapshotName(String pRef){
        return String.format(Locale.FRANCE, "snapshot-%s-%s", pRef,
                new SimpleDateFormat("yyyyMMddHHmmss", Locale.FRANCE).format(new Date()));
    }

    public static String createSnapshotName(){
        return createSnapshotName(new Date());
    }

    public static String createSnapshotName(Date pDate){
        return String.format(Locale.FRANCE, "snapshot-%s", mSDF.format(pDate));
    }
}
