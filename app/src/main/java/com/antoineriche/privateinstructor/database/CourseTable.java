package com.antoineriche.privateinstructor.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.antoineriche.privateinstructor.beans.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CourseTable extends MyDatabaseTable {

    private static final String TABLE_NAME = "courses_table";

    private static final String COL_ID = "ID";
    private static final int NUM_COL_ID = 0;

    private static final String COL_PUPIL_ID = "PUPIL_ID";
    private static final int NUM_COL_PUPIL_ID = 1;

    private static final String COL_DATE = "DATE";
    private static final int NUM_COL_DATE = 2;

    private static final String COL_DURATION = "DURATION";
    private static final int NUM_COL_DURATION = 3;

    private static final String COL_STATE = "STATE";
    private static final int NUM_COL_STATE = 4;

    private static final String COL_MONEY = "MONEY";
    private static final int NUM_COL_MONEY = 5;

    private static final String COL_CHAPTER = "CHAPTER";
    private static final int NUM_COL_CHAPTER= 6;

    private static final String COL_COMMENT = "COMMENT";
    private static final int NUM_COL_COMMENT = 7;


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
    protected String getDeletionString() {
        return String.format(Locale.FRANCE, "DROP TABLE %s;", TABLE_NAME);
    }

    public static long insertCourse(SQLiteDatabase pSQLDatabase, Course course) {
        ContentValues values = new ContentValues();
        values.put(COL_DATE, course.getDate());
        values.put(COL_DURATION, course.getDuration());
        values.put(COL_STATE, course.getState());
        values.put(COL_MONEY, course.getMoney());
        values.put(COL_CHAPTER, course.getChapter());
        values.put(COL_COMMENT, course.getComment());
        values.put(COL_PUPIL_ID, course.getPupilID());
        return pSQLDatabase.insert(TABLE_NAME, null, values);
    }

    public static List<Course> getAllCourses(SQLiteDatabase pSQLDatabase){
        Cursor c = pSQLDatabase.query(TABLE_NAME, FIELDS, null, null, null, null, COL_DATE + " DESC");
        return cursorToListCourses(c);
    }

    private static List<Course> cursorToListCourses(Cursor c) {
        List<Course> list = new ArrayList<>();
        if (c.getCount() > 0) {

            c.moveToFirst();

            while (!c.isAfterLast()) {
                Course course = new Course();
                course.setId(c.getInt(NUM_COL_ID));
                course.setDate(c.getLong(NUM_COL_DATE));
                course.setDuration(c.getInt(NUM_COL_DURATION));
                course.setState(c.getInt(NUM_COL_STATE));
                course.setMoney(c.getDouble(NUM_COL_MONEY));
                course.setChapter(c.getString(NUM_COL_CHAPTER));
                course.setComment(c.getString(NUM_COL_COMMENT));
                course.setPupilID(c.getInt(NUM_COL_PUPIL_ID));

                list.add(course);
                c.moveToNext();
            }

            c.close();
        }

        return list;
    }

}
