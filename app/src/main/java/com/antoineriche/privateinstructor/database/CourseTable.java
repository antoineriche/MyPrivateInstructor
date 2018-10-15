package com.antoineriche.privateinstructor.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.antoineriche.privateinstructor.beans.Course;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CourseTable extends MyDatabaseTable {

    private static final String TABLE_NAME = "courses_table";

    private static final String COL_ID = "ID";
    public static final int NUM_COL_ID = 0;

    public static final String COL_PUPIL_ID = "PUPIL_ID";
    public static final int NUM_COL_PUPIL_ID = 1;

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


    private static final String[] FIELDS = new String[]{COL_ID, COL_PUPIL_ID, COL_DATE,
        COL_DURATION, COL_STATE, COL_MONEY, COL_CHAPTER, COL_COMMENT};

    @Override
    protected String getCreationString() {
        return "CREATE TABLE " + TABLE_NAME + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_DATE + " LONG, "
                + COL_DURATION + " INTEGER, " + COL_STATE + " INTEGER, " + COL_MONEY + " DOUBLE, "
                + COL_CHAPTER + " TEXT, " + COL_COMMENT + " TEXT, " + COL_PUPIL_ID + " INTEGER NOT NULL);";
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

    public static Course getCourseWithId(SQLiteDatabase pSQLDatabase, long id) {
        Cursor c = pSQLDatabase.query(TABLE_NAME, FIELDS, COL_ID + " = " + id, null, null, null, null);
        return cursorToCourse(c, pSQLDatabase);
    }

    public static List<Course> getCoursesForPupil(SQLiteDatabase pSQLDatabase, long pupilId) {
        Cursor c = pSQLDatabase.query(TABLE_NAME, FIELDS, COL_PUPIL_ID + " = " + pupilId, null, null, null, null);
        return cursorToListCourses(c, pSQLDatabase);
    }

    public static List<Course> getCoursesForWeekOffset(SQLiteDatabase pSQLDatabase, int pWeekOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, pWeekOffset);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long startDate = calendar.getTimeInMillis();

        calendar.add(Calendar.DAY_OF_YEAR, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        long endDate = calendar.getTimeInMillis();

        String filterString = String.format(Locale.FRANCE, "%s BETWEEN '%d' AND '%d'", COL_DATE, startDate, endDate);
        String orderString = String.format(Locale.FRANCE, "%s ASC", COL_DATE);

        Cursor c = pSQLDatabase.query(TABLE_NAME, FIELDS, filterString , null, null, null, orderString);
        return cursorToListCourses(c, pSQLDatabase);
    }

    public static List<Course> getAllCourses(SQLiteDatabase pSQLDatabase){
        String orderString = String.format(Locale.FRANCE, "%s DESC", COL_DATE);

        Cursor c = pSQLDatabase.query(TABLE_NAME, FIELDS, null, null, null, null, orderString);
        return cursorToListCourses(c, pSQLDatabase);
    }

    public static boolean removeCourseWithID(SQLiteDatabase pSQLDatabase, long id) {
        return pSQLDatabase.delete(TABLE_NAME, COL_ID + " = " + id, null) == 1;
    }

    private static Course cursorToCourse(Cursor c, SQLiteDatabase pDatabase) {
        Course course = null;

        if (c.getCount() > 0) {
            c.moveToFirst();
            course = new Course(c);
            course.setPupil(PupilTable.getPupilWithId(pDatabase, course.getPupilID()));
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
                course.setPupil(PupilTable.getPupilWithId(pDatabase, course.getPupilID()));
                list.add(course);
                c.moveToNext();
            }

            c.close();
        }

        return list;
    }

}
