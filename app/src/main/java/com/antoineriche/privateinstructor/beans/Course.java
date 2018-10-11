package com.antoineriche.privateinstructor.beans;

import android.content.ContentValues;
import android.database.Cursor;

import com.antoineriche.privateinstructor.database.CourseTable;

import static com.antoineriche.privateinstructor.database.CourseTable.COL_CHAPTER;
import static com.antoineriche.privateinstructor.database.CourseTable.COL_COMMENT;
import static com.antoineriche.privateinstructor.database.CourseTable.COL_DATE;
import static com.antoineriche.privateinstructor.database.CourseTable.COL_DURATION;
import static com.antoineriche.privateinstructor.database.CourseTable.COL_MONEY;
import static com.antoineriche.privateinstructor.database.CourseTable.COL_PUPIL_ID;
import static com.antoineriche.privateinstructor.database.CourseTable.COL_STATE;

public class Course {

    //Statics
    public static final int FORESEEN = 0;
    public static final int VALIDATED = 1;
    public static final int WAITING_FOT_VALIDATION = 2;
    public static final int CANCELED = 3;

    public static final int ALL_COURSES = 3;

    public static final int DURATION_1H30 = 90;
    public static final int DURATION_1H = 60;
    public static final int DURATION_2H = 120;

    // Variables
    protected long id;
    protected long pupilID;             //
    protected long date;
    protected int duration;             //
    protected int state;
    protected double money;             //
    protected String chapter;           //
    protected String comment;           //
    protected Pupil pupil;              //

    public Course() {
    }

    public Course(long date, int duration, int state, double money) {
        this.date = date;
        this.duration = duration;
        this.state = state;
        this.money = money;
    }

    public Course(long date, int duration, int state, double money, String chapter, String comment, long pupilId) {
        this(date, duration, state, money);
        this.pupilID = pupilId;
        this.chapter = chapter;
        this.comment = comment;
    }

    public Course(long id, long pupilID, long date, int duration, int state, double money, String chapter, String comment) {
        this(date, duration, state, money, chapter, comment, pupilID);
        this.id = id;
        this.pupilID = pupilID;
    }

    public Course(Cursor c) {
        this(c.getLong(CourseTable.NUM_COL_ID), c.getLong(CourseTable.NUM_COL_PUPIL_ID),
            c.getLong(CourseTable.NUM_COL_DATE), c.getInt(CourseTable.NUM_COL_DURATION),
            c.getInt(CourseTable.NUM_COL_STATE), c.getDouble(CourseTable.NUM_COL_MONEY),
            c.getString(CourseTable.NUM_COL_CHAPTER), c.getString(CourseTable.NUM_COL_COMMENT)
        );
        this.pupilID = c.getLong(CourseTable.NUM_COL_PUPIL_ID);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPupilID() {
        return pupilID;
    }

    public void setPupilID(long pupilID) {
        this.pupilID = pupilID;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public Pupil getPupil(){
        return this.pupil;
    }

    public void setPupil(Pupil pPupil){
        this.pupil = pPupil;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(COL_DATE, this.date);
        values.put(COL_DURATION, this.duration);
        values.put(COL_STATE, this.state);
        values.put(COL_MONEY, this.money);
        values.put(COL_CHAPTER, this.chapter);
        values.put(COL_COMMENT, this.comment);
        values.put(COL_PUPIL_ID, this.pupilID);
        return values;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", pupilID=" + pupilID +
                ", date=" + date +
                ", duration=" + duration +
                ", state=" + state +
                ", money=" + money +
                ", chapter='" + chapter + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
