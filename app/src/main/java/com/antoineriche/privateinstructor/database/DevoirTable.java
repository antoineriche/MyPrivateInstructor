package com.antoineriche.privateinstructor.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.beans.Devoir;
import com.antoineriche.privateinstructor.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DevoirTable extends MyDatabaseTable {

    public static final String TABLE_NAME = "devoirs_table";

    public static final String COL_ID = "ID";
    public static final int NUM_COL_ID = 0;

    public static final String COL_PUPIL_UUID = "PUPIL_UUID";
    public static final int NUM_COL_PUPIL_UUID = 1;

    public static final String COL_DATE = "DATE";
    public static final int NUM_COL_DATE = 2;

    public static final String COL_MARK = "MARK";
    public static final int NUM_COL_MARK = 3;

    public static final String COL_MAX_MARK = "MAX_MARK";
    public static final int NUM_COL_MAX_MARK = 4;

    public static final String COL_CHAPTER = "CHAPTER";
    public static final int NUM_COL_CHAPTER= 5;

    public static final String COL_COMMENT = "COMMENT";
    public static final int NUM_COL_COMMENT = 6;

    public static final String COL_STATE = "STATE";
    public static final int NUM_COL_STATE = 7;

    public static final String COL_TYPE = "TYPE";
    public static final int NUM_COL_TYPE = 8;

    public static final String COL_UUID = "UUID";
    public static final int NUM_COL_UUID = 9;

    public static final String COL_DURATION = "DURATION";
    public static final int NUM_COL_DURATION = 10;



    private static final String[] FIELDS = new String[]{COL_ID, COL_PUPIL_UUID, COL_DATE,
            COL_MARK, COL_MAX_MARK, COL_CHAPTER, COL_COMMENT, COL_STATE, COL_TYPE, COL_UUID, COL_DURATION};

    private static final String CREATION_STRING = "CREATE TABLE " + TABLE_NAME + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_PUPIL_UUID + " TEXT, "
            + COL_DATE + " LONG, " + COL_MARK + " DOUBLE, " + COL_MAX_MARK + " DOUBLE, "
            + COL_CHAPTER + " TEXT, " + COL_COMMENT + " TEXT, " + COL_STATE + " INTEGER, "
            + COL_TYPE + " INTEGER, " + COL_UUID + " TEXT, " + COL_DURATION + " INTEGER);";

    @Override
    protected String getCreationString() {
        return CREATION_STRING;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    public static long insertDevoir(SQLiteDatabase pSQLDatabase, Devoir devoir) {
        return pSQLDatabase.insert(TABLE_NAME, null, devoir.toContentValues());
    }

    public static int updateDevoir(SQLiteDatabase pSQLDatabase, long id, Devoir devoir) {
        return pSQLDatabase.update(TABLE_NAME, devoir.toContentValues(), COL_ID + " = " + id, null);
    }

    public static boolean removeDevoirWithID(SQLiteDatabase pSQLDatabase, long id) {
        return pSQLDatabase.delete(TABLE_NAME, COL_ID + " = " + id, null) == 1;
    }

    public static Devoir getDevoirWithId(SQLiteDatabase pSQLDatabase, long id) {
        Cursor c = pSQLDatabase.query(TABLE_NAME, FIELDS, COL_ID + " = " + id, null, null, null, null);
        return cursorToDevoir(c, pSQLDatabase);
    }

    public static List<Devoir> getDevoirsForPupil(SQLiteDatabase pSQLDatabase, String pupilUuid) {
        String orderString = String.format(Locale.FRANCE, "%s DESC", COL_DATE);
        Cursor c = pSQLDatabase.query(TABLE_NAME, FIELDS, COL_PUPIL_UUID + " = '" + pupilUuid + "'", null, null, null, orderString);
        return cursorToListDevoirs(c, pSQLDatabase);
    }

    public static List<Devoir> getDevoirsForWeekOffset(SQLiteDatabase pSQLDatabase, int pWeekOffset) {
        Date start = DateUtils.getFirstSecondOfWeekOffsetFromNow(pWeekOffset);
        Date end = DateUtils.getLastSecondOfWeekOffsetFromNow(pWeekOffset);
        return getDevoirsBetweenTwoDates(pSQLDatabase, start, end);
    }

    public static List<Devoir> getDevoirsBetweenTwoDates(SQLiteDatabase pSQLDatabase, Date pStart, Date pEnd){
        return getDevoirsBetweenTwoDates(pSQLDatabase, pStart.getTime(), pEnd.getTime());
    }

    public static List<Devoir> getDevoirsBetweenTwoDates(SQLiteDatabase pSQLDatabase, long pStartInMillis, long pEndInMillis){
        String filterString = String.format(Locale.FRANCE, "%s BETWEEN '%d' AND '%d'", COL_DATE, pStartInMillis, pEndInMillis);
        String orderString = String.format(Locale.FRANCE, "%s ASC", COL_DATE);
        Cursor c = pSQLDatabase.query(TABLE_NAME, FIELDS, filterString , null, null, null, orderString);
        return cursorToListDevoirs(c, pSQLDatabase);
    }

    public static List<Devoir> getAllDevoirs(SQLiteDatabase pSQLDatabase){
        String orderString = String.format(Locale.FRANCE, "%s DESC", COL_DATE);
        Cursor c = pSQLDatabase.query(TABLE_NAME, FIELDS, null, null, null, null, orderString);
        return cursorToListDevoirs(c, pSQLDatabase);
    }

    public static List<Devoir> getDevoirsWithState(SQLiteDatabase pSQLDatabase, int pDevoirState){
        String orderString = String.format(Locale.FRANCE, "%s ASC", COL_DATE);
        String filterString = String.format(Locale.FRANCE, "%s = %d", COL_STATE, pDevoirState);
        Cursor c = pSQLDatabase.query(TABLE_NAME, FIELDS, filterString, null, null, null, orderString);
        return cursorToListDevoirs(c, pSQLDatabase);
    }

    private static long getLastDevoirDate(SQLiteDatabase pSQLDatabase){
        String filterString = String.format(Locale.FRANCE, "%s=(SELECT MAX(%s) FROM %s WHERE %s != %d)", COL_DATE, COL_DATE, TABLE_NAME, COL_STATE, Course.CANCELED);
        Cursor c = pSQLDatabase.query(TABLE_NAME, FIELDS, filterString , null, null, null, null);
        c.moveToFirst();
        return c.getLong(NUM_COL_DATE);
    }

    private static long getFirstDevoirDate(SQLiteDatabase pSQLDatabase){
        String filterString = String.format(Locale.FRANCE, "%s=(SELECT MIN(%s) FROM %s WHERE %s != %d)", COL_DATE, COL_DATE, TABLE_NAME, COL_STATE, Course.CANCELED);
        Cursor c = pSQLDatabase.query(TABLE_NAME, FIELDS, filterString , null, null, null, null);
        c.moveToFirst();
        return c.getLong(NUM_COL_DATE);
    }

    public static double getMonthlyDevoirCountMean(SQLiteDatabase pSQLDatabase){
        long pStart = getFirstDevoirDate(pSQLDatabase);
        long pEnd = getLastDevoirDate(pSQLDatabase);
        return (double) getAllDevoirs(pSQLDatabase).size() / (double) DateUtils.getMonthCountBetweenDates(pStart, pEnd);
    }

    public static void clearTable(SQLiteDatabase pSQLDatabase){
        pSQLDatabase.execSQL("DROP TABLE " + TABLE_NAME);
        pSQLDatabase.execSQL(CREATION_STRING);
    }

    private static Devoir cursorToDevoir(Cursor c, SQLiteDatabase pDatabase) {
        Devoir devoir = null;

        if (c.getCount() > 0) {
            c.moveToFirst();
            devoir = new Devoir(c);
            devoir.setPupil(PupilTable.getPupilWithId(pDatabase, devoir.getPupilUuid()));
            c.close();
        }

        return devoir;
    }

    private static List<Devoir> cursorToListDevoirs(Cursor c, SQLiteDatabase pDatabase) {
        List<Devoir> list = new ArrayList<>();
        if (c.getCount() > 0) {

            c.moveToFirst();

            while (!c.isAfterLast()) {
                Devoir devoir = new Devoir(c);
                devoir.setPupil(PupilTable.getPupilWithId(pDatabase, devoir.getPupilUuid()));
                list.add(devoir);
                c.moveToNext();
            }

            c.close();
        }

        return list;
    }

}
