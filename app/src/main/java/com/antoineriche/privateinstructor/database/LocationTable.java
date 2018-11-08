package com.antoineriche.privateinstructor.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.antoineriche.privateinstructor.beans.Devoir;
import com.antoineriche.privateinstructor.beans.Location;

import java.util.ArrayList;
import java.util.List;

public class LocationTable extends MyDatabaseTable {

    public static final String TABLE_NAME = "location_table";

    private static final String COL_ID = "ID";
    public static final int NUM_COL_ID = 0;

    public static final String COL_ADDRESS = "ADDRESS";
    public static final int NUM_COL_ADDRESS = 1;

    public static final String COL_LATITUDE = "LATITUDE";
    public static final int NUM_COL_LATITUDE = 2;

    public static final String COL_LONGITUDE = "LONGITUDE";
    public static final int NUM_COL_LONGITUDE = 3;

    public static final String COL_UUID = "UUID";
    public static final int NUM_COL_UUID = 4;


    private static final String[] FIELDS = new String[]{COL_ID, COL_ADDRESS, COL_LATITUDE, COL_LONGITUDE, COL_UUID};

    private static final String CREATION_STRING = "CREATE TABLE " + TABLE_NAME + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_ADDRESS + " TEXT, "
            + COL_LATITUDE + " DOUBLE, " + COL_LONGITUDE + " DOUBLE, "
            + COL_UUID + " TEXT);";

    @Override
    protected String getCreationString() {
        return CREATION_STRING;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    public static String insertLocation(SQLiteDatabase pSQLDatabase, Location location) {
        if(pSQLDatabase.insert(TABLE_NAME, null, location.toContentValues()) > -1){
            return location.getUuid();
        }
        else{
            return null;
        }
    }

    public static int updateLocationWithUuid(SQLiteDatabase pSQLDatabase, String uUID, Location location) {
        return pSQLDatabase.update(TABLE_NAME, location.toContentValues(), COL_UUID + " = '" + uUID + "'", null);
    }

    public static boolean removeLocationWithID(SQLiteDatabase pSQLDatabase, long id) {
        return pSQLDatabase.delete(TABLE_NAME, COL_ID + " = " + id, null) == 1;
    }

    public static Location getLocationWithUuid(SQLiteDatabase pSQLDatabase, String pUuid) {
        Cursor c = pSQLDatabase.query(TABLE_NAME, FIELDS, COL_UUID + " = '" + pUuid + "'", null, null, null, null);
        return cursorToLocation(c);
    }

    public static List<Location> getAllLocations(SQLiteDatabase pSQLDatabase){
        Cursor c = pSQLDatabase.query(TABLE_NAME, FIELDS, null, null, null, null, null);
        return cursorToListLocations(c);
    }

    public static void clearTable(SQLiteDatabase pSQLDatabase){
        pSQLDatabase.execSQL("DROP TABLE " + TABLE_NAME);
        pSQLDatabase.execSQL(CREATION_STRING);
    }

    private static Location cursorToLocation(Cursor c) {
        Location location = null;
        if (c.getCount() > 0) {
            c.moveToFirst();
            location = new Location(c);
            c.close();
        }

        return location;
    }

    private static List<Location> cursorToListLocations(Cursor c) {

        List<Location> list = new ArrayList<>();

        if (c.getCount() > 0) {
            c.moveToFirst();

            while (!c.isAfterLast()) {
                list.add(new Location(c));
                c.moveToNext();
            }
            c.close();
        }
        return list;
    }
}
