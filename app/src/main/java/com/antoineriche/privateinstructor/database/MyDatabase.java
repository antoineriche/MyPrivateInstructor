package com.antoineriche.privateinstructor.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.antoineriche.privateinstructor.beans.Pupil;

public class MyDatabase extends SQLiteOpenHelper {

    public static final int VERSION = 13;
    public static final String DB_NAME = "private-instructor";

    public MyDatabase(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DB_NAME, factory, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(getClass().getSimpleName(), "onCreate");
        sqLiteDatabase.execSQL(new CourseTable().getCreationString());
        sqLiteDatabase.execSQL(new PupilTable().getCreationString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if(newVersion > oldVersion) {
            Log.e(getClass().getSimpleName(), "onUpgrade");
//            sqLiteDatabase.execSQL(new LocationTable().getCreationString());
//            sqLiteDatabase.execSQL(new PupilTable().getCreationString());

            //TODO update version
//            sqLiteDatabase.execSQL("ALTER TABLE " + CourseTable.TABLE_NAME + " ADD COLUMN " + CourseTable.COL_PUPIL_UUID + " TEXT DEFAULT ''");
//            sqLiteDatabase.execSQL("ALTER TABLE " + PupilTable.TABLE_NAME + " ADD COLUMN " + PupilTable.COL_LOCATION_UUID + " TEXT DEFAULT ''");
            //sqLiteDatabase.execSQL("ALTER TABLE " + LocationTable.TABLE_NAME + " ADD COLUMN " + LocationTable.COL_UUID + " TEXT DEFAULT ''");

//            String temp = "tempo-courses";
//            sqLiteDatabase.execSQL(CourseTable.creat(temp));
//            Log.e(getClass().getSimpleName(), temp + " created");
//
//            sqLiteDatabase.execSQL("INSERT INTO " + temp + " SELECT " + CourseTable.COL_ID + "," + CourseTable.COL_PUPIL_UUID
//                    + "," + CourseTable.COL_DATE + "," + CourseTable.COL_DURATION + "," + CourseTable.COL_STATE
//                    + "," + CourseTable.COL_MONEY + "," + CourseTable.COL_CHAPTER + ", " + CourseTable.COL_COMMENT
//                    + "," + CourseTable.COL_UUID + " FROM " + CourseTable.TABLE_NAME);
//
//            Log.e(getClass().getSimpleName(), temp + " copied from " + CourseTable.TABLE_NAME);
//
//            sqLiteDatabase.execSQL("DROP TABLE " + CourseTable.TABLE_NAME);
//            Log.e(getClass().getSimpleName(), CourseTable.TABLE_NAME + " dropped");
//
//            sqLiteDatabase.execSQL(CourseTable.creat(CourseTable.TABLE_NAME));
//            Log.e(getClass().getSimpleName(), CourseTable.TABLE_NAME + " created");
//
//            sqLiteDatabase.execSQL("INSERT INTO " + CourseTable.TABLE_NAME + " SELECT " + CourseTable.COL_ID + "," + CourseTable.COL_PUPIL_UUID
//                    + "," + CourseTable.COL_DATE + "," + CourseTable.COL_DURATION + "," + CourseTable.COL_STATE
//                    + "," + CourseTable.COL_MONEY + "," + CourseTable.COL_CHAPTER + ", " + CourseTable.COL_COMMENT
//                    + "," + CourseTable.COL_UUID + " FROM " + temp);
//
//            sqLiteDatabase.execSQL("DROP TABLE " +temp);
//            Log.e(getClass().getSimpleName(), temp + " dropped");



//            String temp = "tempo_pupils";
//            sqLiteDatabase.execSQL(PupilTable.creat(temp));
//            Log.e(getClass().getSimpleName(), temp + " created");
//
//            sqLiteDatabase.execSQL("INSERT INTO " + temp + " SELECT " + PupilTable.COL_ID + "," + PupilTable.COL_FIRST_NAME
//                    + "," + PupilTable.COL_LAST_NAME + "," + PupilTable.COL_GENDER + "," + PupilTable.COL_CLASS_LEVEL
//                    + "," + PupilTable.COL_PAYMENT_TYPE + "," + PupilTable.COL_FREQUENCY
//                    + "," + PupilTable.COL_LOCATION_UUID + "," + PupilTable.COL_HOURLY_PRICE
//                    + "," + PupilTable.COL_DATE_SINCE + "," + PupilTable.COL_PHONE + "," + PupilTable.COL_PARENT_PHONE
//                    + "," + PupilTable.COL_IMG_PATH + "," + PupilTable.COL_STATE + "," + PupilTable.COL_UUID + " FROM " + PupilTable.TABLE_NAME);
//
//            Log.e(getClass().getSimpleName(), temp + " copied from " + PupilTable.TABLE_NAME);
//
//            sqLiteDatabase.execSQL("DROP TABLE " + PupilTable.TABLE_NAME);
//            Log.e(getClass().getSimpleName(), PupilTable.TABLE_NAME + " dropped");
//
//            sqLiteDatabase.execSQL(PupilTable.creat(PupilTable.TABLE_NAME));
//            Log.e(getClass().getSimpleName(), PupilTable.TABLE_NAME + " created");
//
//            sqLiteDatabase.execSQL("INSERT INTO " + PupilTable.TABLE_NAME + " SELECT " + PupilTable.COL_ID + "," + PupilTable.COL_FIRST_NAME
//                    + "," + PupilTable.COL_LAST_NAME + "," + PupilTable.COL_GENDER + "," + PupilTable.COL_CLASS_LEVEL
//                    + "," + PupilTable.COL_PAYMENT_TYPE + "," + PupilTable.COL_FREQUENCY
//                    + "," + PupilTable.COL_LOCATION_UUID + "," + PupilTable.COL_HOURLY_PRICE
//                    + "," + PupilTable.COL_DATE_SINCE + "," + PupilTable.COL_PHONE + "," + PupilTable.COL_PARENT_PHONE
//                    + "," + PupilTable.COL_IMG_PATH + "," + PupilTable.COL_STATE + "," + PupilTable.COL_UUID + " FROM " + temp);
//
//            sqLiteDatabase.execSQL("DROP TABLE " +temp);
//            Log.e(getClass().getSimpleName(), temp + " dropped");

        }
    }

}
