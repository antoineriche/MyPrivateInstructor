package com.antoineriche.privateinstructor.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.antoineriche.privateinstructor.beans.Pupil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PupilTable extends MyDatabaseTable {

    private static final String TABLE_NAME = "pupils_table";

    private static final String COL_ID = "ID";
    public static final int NUM_COL_ID = 0;

    public static final String COL_FIRSTNAME = "FIRST_NAME";
    public static final int NUM_COL_FIRSTNAME = 1;

    public static final String COL_LASTNAME = "LAST_NAME";
    public static final int NUM_COL_LASTNAME = 2;

    public static final String COL_GENDER = "GENDER";
    public static final int NUM_COL_GENDER = 3;

    public static final String COL_CLASSLEVEL = "CLASS_LEVEL";
    public static final int NUM_COL_CLASSLEVEL = 4;

    public static final String COL_PAYMENT_TYPE = "PAYMENT_TYPE";
    public static final int NUM_COL_PAYMENT_TYPE = 5;

    public static final String COL_FREQUENCY = "FREQUENCY";
    public static final int NUM_COL_FREQUENCY = 6;

    public static final String COL_ADDRESS = "ADDRESS";
    public static final int NUM_COL_ADDRESS = 7;

    public static final String COL_HOURLY_PRICE = "HOURLY_PRICE";
    public static final int NUM_COL_HOURLY_PRICE = 8;

    public static final String COL_DATE_SINCE = "SINCE";
    public static final int NUM_COL_DATE_SINCE = 9;

    public static final String COL_PHONE = "PHONE";
    public static final int NUM_COL_PHONE = 10;

    public static final String COL_PARENT_PHONE = "PARENT_PHONE";
    public static final int NUM_COL_PARENT_PHONE = 11;

    public static final String COL_IMG_PATH = "IMG_PATH";
    public static final int NUM_COL_IMG_PATH = 12;

    public static final String COL_STATE = "STATE";
    public static final int NUM_COL_STATE = 13;


    private static final String[] FIELDS = new String[]{COL_ID, COL_FIRSTNAME, COL_LASTNAME, COL_GENDER, COL_CLASSLEVEL,
            COL_PAYMENT_TYPE, COL_FREQUENCY, COL_ADDRESS, COL_HOURLY_PRICE, COL_DATE_SINCE, COL_PHONE,
            COL_PARENT_PHONE, COL_IMG_PATH, COL_STATE};

    @Override
    protected String getCreationString() {
        return "CREATE TABLE " + TABLE_NAME + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_FIRSTNAME + " TEXT NOT NULL, "
                + COL_LASTNAME + " TEXT NOT NULL, "
                + COL_GENDER + " INTEGER, " + COL_CLASSLEVEL + " INTEGER, "
                + COL_PAYMENT_TYPE + " INTEGER, " + COL_FREQUENCY + " INTEGER, "
                + COL_ADDRESS + " TEXT, " + COL_HOURLY_PRICE + " DOUBLE, "
                + COL_DATE_SINCE + " LONG DEFAULT " + System.currentTimeMillis() + ", "
                + COL_PHONE + " TEXT, " + COL_PARENT_PHONE + " TEXT, "
                + COL_IMG_PATH + " TEXT, " + COL_STATE + " INTEGER DEFAULT " + Pupil.ACTIVE+ ");";
    }

    @Override
    protected String getDeletionString() {
        return String.format(Locale.FRANCE, "DROP TABLE %s;", TABLE_NAME);
    }

    public static long insertPupil(SQLiteDatabase pSQLDatabase, Pupil pupil) {
        pupil.setSinceDate(System.currentTimeMillis());
        return pSQLDatabase.insert(TABLE_NAME, null, pupil.toContentValues());
    }

    public static int updatePupil(SQLiteDatabase pSQLDatabase, long id, Pupil pupil){
        return pSQLDatabase.update(TABLE_NAME, pupil.toContentValues(), COL_ID + " = " + id, null);
    }

    public static Pupil getPupilWithId(SQLiteDatabase pSQLDatabase, long id) {
        Cursor c = pSQLDatabase.query(TABLE_NAME, FIELDS, COL_ID + " = " + id, null, null, null, null);
        return cursorToPupil(c);
    }

    public static List<Pupil> getAllPupils(SQLiteDatabase pSQLDatabase){
        Cursor c = pSQLDatabase.query(TABLE_NAME, FIELDS, null, null, null, null, COL_DATE_SINCE);
        return cursorToListPupils(c);
    }

    public static boolean removePupilWithID(SQLiteDatabase pSQLDatabase, long id) {
        return pSQLDatabase.delete(TABLE_NAME, COL_ID + " = " + id, null) == 1;
    }

    private static Pupil cursorToPupil(Cursor c) {
        Pupil pupil = null;

        if (c.getCount() > 0) {
            c.moveToFirst();
            pupil = new Pupil(c);
            c.close();
        }

        return pupil;
    }

    private static List<Pupil> cursorToListPupils(Cursor c) {

        List<Pupil> list = new ArrayList<>();

        if (c.getCount() > 0) {
            c.moveToFirst();

            while (!c.isAfterLast()) {
                list.add(new Pupil(c));
                c.moveToNext();
            }
            c.close();
        }
        return list;
    }

}
