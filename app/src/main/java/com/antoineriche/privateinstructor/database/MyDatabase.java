package com.antoineriche.privateinstructor.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDatabase extends SQLiteOpenHelper {

    public static final int VERSION = 6;
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
            sqLiteDatabase.execSQL(new LocationTable().getCreationString());

            //TODO
//            sqLiteDatabase.execSQL(PupilTable.newCreation(PupilTable.TABLE_NAME_2));
//            Log.e(getClass().getSimpleName(), "pupil_table_2 created");
//
//            sqLiteDatabase.execSQL("INSERT INTO " + PupilTable.TABLE_NAME_2 + " SELECT " + PupilTable.COL_ID + "," + PupilTable.COL_FIRST_NAME
//                    + "," + PupilTable.COL_LAST_NAME + "," + PupilTable.COL_GENDER + "," + PupilTable.COL_CLASS_LEVEL
//                    + "," + PupilTable.COL_PAYMENT_TYPE + "," + PupilTable.COL_FREQUENCY + ", 0," + PupilTable.COL_HOURLY_PRICE
//                    + "," + PupilTable.COL_DATE_SINCE + "," + PupilTable.COL_PHONE + "," + PupilTable.COL_PARENT_PHONE
//                    + "," + PupilTable.COL_IMG_PATH + "," + PupilTable.COL_STATE + " FROM " + PupilTable.TABLE_NAME);
//            Log.e(getClass().getSimpleName(), "pupil_table_2 copied from pupil_table");
//
//            sqLiteDatabase.execSQL("DROP TABLE " + PupilTable.TABLE_NAME);
//            Log.e(getClass().getSimpleName(), "pupil_table dropped");
//
//            sqLiteDatabase.execSQL(PupilTable.newCreation(PupilTable.TABLE_NAME));
//            Log.e(getClass().getSimpleName(), "pupil_table created");
//
//            sqLiteDatabase.execSQL("INSERT INTO " + PupilTable.TABLE_NAME + " SELECT " + PupilTable.COL_ID + "," + PupilTable.COL_FIRST_NAME
//                    + "," + PupilTable.COL_LAST_NAME + "," + PupilTable.COL_GENDER + "," + PupilTable.COL_CLASS_LEVEL
//                    + "," + PupilTable.COL_PAYMENT_TYPE + "," + PupilTable.COL_FREQUENCY + ", 0," + PupilTable.COL_HOURLY_PRICE
//                    + "," + PupilTable.COL_DATE_SINCE + "," + PupilTable.COL_PHONE + "," + PupilTable.COL_PARENT_PHONE
//                    + "," + PupilTable.COL_IMG_PATH + "," + PupilTable.COL_STATE + " FROM " + PupilTable.TABLE_NAME_2);
//            Log.e(getClass().getSimpleName(), "pupil_table copied from pupil_table_2");
//
//            sqLiteDatabase.execSQL("DROP TABLE " + PupilTable.TABLE_NAME_2);
//            Log.e(getClass().getSimpleName(), "pupil_table_2 dropped");
        }
    }

}
