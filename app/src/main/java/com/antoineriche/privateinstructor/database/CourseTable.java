package com.antoineriche.privateinstructor.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.utils.CourseUtils;
import com.antoineriche.privateinstructor.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CourseTable extends MyDatabaseTable {

    public static final String TABLE_NAME = "courses_table";

    public static final String COL_ID = "ID";
    public static final int NUM_COL_ID = 0;

    public static final String COL_PUPIL_UUID = "PUPIL_UUID";
    public static final int NUM_COL_PUPIL_UUID = 1;

    public static final String COL_DATE = "DATE";
    public static final int NUM_COL_DATE = 2;

    public static final String COL_DURATION = "DURATION";
    public static final int NUM_COL_DURATION = 3;

    public static final String COL_STATE = "STATE";
    public static final int NUM_COL_STATE = 4;

    public static final String COL_MONEY = "MONEY";
    public static final int NUM_COL_MONEY = 5;

    public static final String COL_CHAPTER = "CHAPTER";
    public static final int NUM_COL_CHAPTER= 6;

    public static final String COL_COMMENT = "COMMENT";
    public static final int NUM_COL_COMMENT = 7;

    public static final String COL_UUID = "UUID";
    public static final int NUM_COL_UUID = 8;



    private static final String[] FIELDS = new String[]{COL_ID, COL_PUPIL_UUID, COL_DATE,
            COL_DURATION, COL_STATE, COL_MONEY, COL_CHAPTER, COL_COMMENT, COL_UUID};

    private static final String CREATION_STRING = "CREATE TABLE " + TABLE_NAME + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_PUPIL_UUID + " TEXT, "
            + COL_DATE + " LONG, " + COL_DURATION + " INTEGER, " + COL_STATE + " INTEGER, "
            + COL_MONEY + " DOUBLE, " + COL_CHAPTER + " TEXT, " + COL_COMMENT + " TEXT, "
            + COL_UUID + " TEXT);";

    @Override
    protected String getCreationString() {
        return CREATION_STRING;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    public static long insertCourse(SQLiteDatabase pSQLDatabase, Course course) {
        return pSQLDatabase.insert(TABLE_NAME, null, course.toContentValues());
    }

    public static int updateCourse(SQLiteDatabase pSQLDatabase, long id, Course course){
        return pSQLDatabase.update(TABLE_NAME, course.toContentValues(), COL_ID + " = " + id, null);
    }

    public static boolean removeCourseWithID(SQLiteDatabase pSQLDatabase, long id) {
        return pSQLDatabase.delete(TABLE_NAME, COL_ID + " = " + id, null) == 1;
    }

    public static Course getCourseWithId(SQLiteDatabase pSQLDatabase, long id) {
        Cursor c = pSQLDatabase.query(TABLE_NAME, FIELDS, COL_ID + " = " + id, null, null, null, null);
        return cursorToCourse(c, pSQLDatabase);
    }

    public static List<Course> getCoursesForPupil(SQLiteDatabase pSQLDatabase, String pupilUuid) {
        String orderString = String.format(Locale.FRANCE, "%s DESC", COL_DATE);
        Cursor c = pSQLDatabase.query(TABLE_NAME, FIELDS, COL_PUPIL_UUID + " = '" + pupilUuid + "'", null, null, null, orderString);
        return cursorToListCourses(c, pSQLDatabase);
    }

    public static List<Course> getCoursesBetweenTwoDates(SQLiteDatabase pSQLDatabase, Date pStart, Date pEnd){
        return getCoursesBetweenTwoDates(pSQLDatabase, pStart.getTime(), pEnd.getTime());
    }

    public static List<Course> getCoursesBetweenTwoDates(SQLiteDatabase pSQLDatabase, long pStartInMillis, long pEndInMillis){
        String filterString = String.format(Locale.FRANCE, "%s BETWEEN '%d' AND '%d'", COL_DATE, pStartInMillis, pEndInMillis);
        String orderString = String.format(Locale.FRANCE, "%s ASC", COL_DATE);
        Cursor c = pSQLDatabase.query(TABLE_NAME, FIELDS, filterString , null, null, null, orderString);
        return cursorToListCourses(c, pSQLDatabase);
    }

    public static List<Course> getCoursesForWeekOffset(SQLiteDatabase pSQLDatabase, int pWeekOffset) {
        Date start = DateUtils.getFirstSecondOfWeekOffsetFromNow(pWeekOffset);
        Date end = DateUtils.getLastSecondOfWeekOffsetFromNow(pWeekOffset);
        return getCoursesBetweenTwoDates(pSQLDatabase, start, end);
    }

    public static List<Course> getAllCourses(SQLiteDatabase pSQLDatabase){
        String orderString = String.format(Locale.FRANCE, "%s DESC", COL_DATE);
        Cursor c = pSQLDatabase.query(TABLE_NAME, FIELDS, null, null, null, null, orderString);
        return cursorToListCourses(c, pSQLDatabase);
    }

    public static List<Course> getCoursesWithState(SQLiteDatabase pSQLDatabase, int pCourseState){
        String orderString = String.format(Locale.FRANCE, "%s ASC", COL_DATE);
        String filterString = String.format(Locale.FRANCE, "%s = %d", COL_STATE, pCourseState);
        Cursor c = pSQLDatabase.query(TABLE_NAME, FIELDS, filterString, null, null, null, orderString);
        return cursorToListCourses(c, pSQLDatabase);
    }

    private static long getLastCourseDate(SQLiteDatabase pSQLDatabase){
        String filterString = String.format(Locale.FRANCE, "%s=(SELECT MAX(%s) FROM %s WHERE %s != %d)", COL_DATE, COL_DATE, TABLE_NAME, COL_STATE, Course.CANCELED);
        Cursor c = pSQLDatabase.query(TABLE_NAME, FIELDS, filterString , null, null, null, null);
        c.moveToFirst();
        return c.getLong(NUM_COL_DATE);
    }

    private static long getFirstCourseDate(SQLiteDatabase pSQLDatabase){
        String filterString = String.format(Locale.FRANCE, "%s=(SELECT MIN(%s) FROM %s WHERE %s != %d)", COL_DATE, COL_DATE, TABLE_NAME, COL_STATE, Course.CANCELED);
        Cursor c = pSQLDatabase.query(TABLE_NAME, FIELDS, filterString , null, null, null, null);
        c.moveToFirst();
        return c.getLong(NUM_COL_DATE);
    }

    public static Course getNextCourse(SQLiteDatabase pSQLDatabase){
        String orderString = String.format(Locale.FRANCE, "%s ASC", COL_DATE);
        String filterString = String.format(Locale.FRANCE, "%s=(SELECT MIN(%s) FROM %s WHERE %s = %d)", COL_DATE, COL_DATE, TABLE_NAME, COL_STATE, Course.FORESEEN);
        Cursor c = pSQLDatabase.query(TABLE_NAME, FIELDS, filterString, null, null, null, orderString);
        return cursorToCourse(c, pSQLDatabase);
    }

    public static double getMonthlyCourseCountMean(SQLiteDatabase pSQLDatabase){
        long pStart = getFirstCourseDate(pSQLDatabase);
        long pEnd = getLastCourseDate(pSQLDatabase);
        return (double) getAllCourses(pSQLDatabase).size() / (double) DateUtils.getMonthCountBetweenDates(pStart, pEnd);
    }

    public static double getMonthlyMoneyMean(SQLiteDatabase pSQLDatabase){
        long pStart = getFirstCourseDate(pSQLDatabase);
        long pEnd = getLastCourseDate(pSQLDatabase);
        return CourseUtils.extractMoneySum(getAllCourses(pSQLDatabase)) / (double) DateUtils.getMonthCountBetweenDates(pStart, pEnd);
    }

    public static void clearTable(SQLiteDatabase pSQLDatabase){
        pSQLDatabase.execSQL("DROP TABLE " + TABLE_NAME);
        pSQLDatabase.execSQL(CREATION_STRING);
    }

    private static Course cursorToCourse(Cursor c, SQLiteDatabase pDatabase) {
        Course course = null;

        if (c.getCount() > 0) {
            c.moveToFirst();
            course = new Course(c);
            course.setPupil(PupilTable.getPupilWithId(pDatabase, course.getPupilUuid()));
            c.close();
        }

        return course;
    }

    private static List<Course> cursorToListCourses(Cursor c, SQLiteDatabase pDatabase) {
        List<Course> list = new ArrayList<>();
        if (c.getCount() > 0) {

            c.moveToFirst();

            while (!c.isAfterLast()) {
                Course course = new Course(c);
                course.setPupil(PupilTable.getPupilWithId(pDatabase, course.getPupilUuid()));
                list.add(course);
                c.moveToNext();
            }

            c.close();
        }

        return list;
    }

}
