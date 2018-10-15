package com.antoineriche.privateinstructor.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.antoineriche.privateinstructor.beans.Location;

public class LocationTable extends MyDatabaseTable {

    private static final String TABLE_NAME = "location_table";

    private static final String COL_ID = "ID";
    public static final int NUM_COL_ID = 0;

    public static final String COL_ADDRESS = "ADDRESS";
    public static final int NUM_COL_ADDRESS = 1;

    public static final String COL_LATITUDE = "LATITUDE";
    public static final int NUM_COL_LATITUDE = 2;

    public static final String COL_LONGITUDE = "LONGITUDE";
    public static final int NUM_COL_LONGITUDE = 3;


    private static final String[] FIELDS = new String[]{COL_ID, COL_ADDRESS, COL_LATITUDE, COL_LONGITUDE};

    @Override
    protected String getCreationString() {
        return "CREATE TABLE " + TABLE_NAME + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_ADDRESS + " TEXT, "
                + COL_LATITUDE + " DOUBLE, " + COL_LONGITUDE + " DOUBLE);";
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    public static long insertLocation(SQLiteDatabase pSQLDatabase, Location location) {
        return pSQLDatabase.insert(TABLE_NAME, null, location.toContentValues());
    }

    public static int updateLocation(SQLiteDatabase pSQLDatabase, long id, Location location){
        return pSQLDatabase.update(TABLE_NAME, location.toContentValues(), COL_ID + " = " + id, null);
    }

    public static boolean removeLocationWithID(SQLiteDatabase pSQLDatabase, long id) {
        return pSQLDatabase.delete(TABLE_NAME, COL_ID + " = " + id, null) == 1;
    }

    public static Location getLocationWithId(SQLiteDatabase pSQLDatabase, long id) {
        Cursor c = pSQLDatabase.query(TABLE_NAME, FIELDS, COL_ID + " = " + id, null, null, null, null);
        return cursorToLocation(c);
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
}
