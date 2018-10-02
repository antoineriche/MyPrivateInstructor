package com.antoineriche.privateinstructor.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.beans.Pupil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PupilTable extends MyDatabaseTable {

    private static final String TABLE_NAME = "pupils_table";

    private static final String COL_ID = "ID";
    private static final int NUM_COL_ID = 0;

    private static final String COL_FIRSTNAME = "FIRST_NAME";
    private static final int NUM_COL_FIRSTNAME = 1;

    private static final String COL_LASTNAME = "LAST_NAME";
    private static final int NUM_COL_LASTNAME = 2;

    public static final String COL_GENDER = "GENDER";
    private static final int NUM_COL_GENDER = 3;

    public static final String COL_CLASSLEVEL = "CLASS_LEVEL";
    private static final int NUM_COL_CLASSLEVEL = 4;

    public static final String COL_PAYMENT_TYPE = "PAYMENT_TYPE";
    private static final int NUM_COL_PAYMENT_TYPE = 5;

    public static final String COL_FREQUENCY = "FREQUENCY";
    private static final int NUM_COL_FREQUENCY = 6;

    public static final String COL_ADDRESS = "ADDRESS";
    private static final int NUM_COL_ADDRESS = 7;

    public static final String COL_HOURLY_PRICE = "HOURLY_PRICE";
    private static final int NUM_COL_HOURLY_PRICE = 8;

    public static final String COL_DATE_SINCE = "SINCE";
    private static final int NUM_COL_DATE_SINCE = 9;

    public static final String COL_PHONE = "PHONE";
    private static final int NUM_COL_PHONE = 10;

    public static final String COL_PARENT_PHONE = "PARENT_PHONE";
    private static final int NUM_COL_PARENT_PHONE = 11;

    public static final String COL_IMG_PATH = "IMG_PATH";
    private static final int NUM_COL_IMG_PATH = 12;

    public static final String COL_STATE = "STATE";
    private static final int NUM_COL_STATE = 13;


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
        ContentValues values = new ContentValues();
        values.put(COL_FIRSTNAME, pupil.getFirstname());
        values.put(COL_LASTNAME, pupil.getLastname());
        values.put(COL_GENDER, pupil.getGender());
        values.put(COL_CLASSLEVEL, pupil.getClassLevel());
        values.put(COL_PAYMENT_TYPE, pupil.getParentPhone());
        values.put(COL_FREQUENCY, pupil.getFrequency());
        values.put(COL_ADDRESS, pupil.getAddress());
        values.put(COL_HOURLY_PRICE, pupil.getHourlyPrice());
        values.put(COL_DATE_SINCE, new Date().getTime());
        values.put(COL_PHONE, pupil.getPhone());
        values.put(COL_PARENT_PHONE, pupil.getParentPhone());
        values.put(COL_IMG_PATH, pupil.getImgPath());
        values.put(COL_STATE, pupil.getState());
        return pSQLDatabase.insert(TABLE_NAME, null, values);
    }

    public static List<Pupil> getAllPupils(SQLiteDatabase pSQLDatabase){
        Cursor c = pSQLDatabase.query(TABLE_NAME, FIELDS, null, null, null, null, COL_DATE_SINCE);
        return cursorToListPupils(c);
    }

    public static List<Pupil> cursorToListPupils(Cursor c) {

        List<Pupil> list = new ArrayList<>();
        if (c.getCount() == 0)
            return list;

        c.moveToFirst();
        while (!c.isAfterLast()) {
            Pupil pupil = new Pupil();
            pupil.setId(c.getInt(NUM_COL_ID));
            pupil.setFirstname(c.getString(NUM_COL_FIRSTNAME));
            pupil.setLastname(c.getString(NUM_COL_LASTNAME));
            pupil.setGender(c.getInt(NUM_COL_GENDER));
            pupil.setClassLevel(c.getInt(NUM_COL_LASTNAME));
            pupil.setPaymentType(c.getInt(NUM_COL_PAYMENT_TYPE));
            pupil.setFrequency(c.getInt(NUM_COL_FREQUENCY));
            pupil.setAddress(c.getString(NUM_COL_ADDRESS));
            pupil.setHourlyPrice(c.getDouble(NUM_COL_HOURLY_PRICE));
            pupil.setSinceDate(c.getLong(NUM_COL_DATE_SINCE));
            pupil.setPhone(c.getString(NUM_COL_PHONE));
            pupil.setParentPhone(c.getString(NUM_COL_PARENT_PHONE));
            pupil.setImgPath(c.getString(NUM_COL_IMG_PATH));
            pupil.setState(c.getInt(NUM_COL_STATE));

            list.add(pupil);
            c.moveToNext();
        }

        c.close();
        return list;
    }

}
