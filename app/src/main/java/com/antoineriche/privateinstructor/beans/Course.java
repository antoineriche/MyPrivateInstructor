package com.antoineriche.privateinstructor.beans;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.utils.DateUtils;
import com.antoineriche.privateinstructor.utils.StringUtils;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.antoineriche.privateinstructor.database.CourseTable.COL_CHAPTER;
import static com.antoineriche.privateinstructor.database.CourseTable.COL_COMMENT;
import static com.antoineriche.privateinstructor.database.CourseTable.COL_DATE;
import static com.antoineriche.privateinstructor.database.CourseTable.COL_DURATION;
import static com.antoineriche.privateinstructor.database.CourseTable.COL_MONEY;
import static com.antoineriche.privateinstructor.database.CourseTable.COL_PUPIL_UUID;
import static com.antoineriche.privateinstructor.database.CourseTable.COL_STATE;
import static com.antoineriche.privateinstructor.database.CourseTable.COL_UUID;

public class Course implements Serializable, DatabaseItem, EventItem {

    // Variables
    protected long id;
    protected long date;
    protected int duration;             //
    protected int state;
    protected double money;             //
    protected String chapter;           //
    protected String comment;           //
    protected Pupil pupil;              //
    protected String uuid;              //
    protected String pupilUuid;         //

    public Course() {
    }

    //Use to retrieve and remove malformed data on Firebase
    public Course(long pId) {
        this.id = pId;
    }

    public Course(long date, int duration, int state, double money) {
        this.date = date;
        this.duration = duration;
        this.state = state;
        this.money = money;
    }

    public Course(long date, int duration, int state, double money, String chapter, String comment, String pupilUuid) {
        this(date, duration, state, money);
        this.pupilUuid = pupilUuid;
        this.chapter = chapter;
        this.comment = comment;
    }

    public Course(long id, String pupilUuid, long date, int duration, int state, double money, String chapter, String comment) {
        this(date, duration, state, money, chapter, comment, pupilUuid);
        this.id = id;
    }

    public Course(Cursor c) {
        this(c.getLong(CourseTable.NUM_COL_ID), c.getString(CourseTable.NUM_COL_PUPIL_UUID),
                c.getLong(CourseTable.NUM_COL_DATE), c.getInt(CourseTable.NUM_COL_DURATION),
                c.getInt(CourseTable.NUM_COL_STATE), c.getDouble(CourseTable.NUM_COL_MONEY),
                c.getString(CourseTable.NUM_COL_CHAPTER), c.getString(CourseTable.NUM_COL_COMMENT)
        );
        this.uuid = c.getString(CourseTable.NUM_COL_UUID);
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    @Override
    public long getDate() {
        return date;
    }

    @Override
    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public int getState() {
        return state;
    }

    @Override
    public void setState(int state) {
        this.state = state;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPupilUuid() {
        return pupilUuid;
    }

    public void setPupilUuid(String pupilUuid) {
        this.pupilUuid = pupilUuid;
    }

    @Exclude
    public Pupil getPupil(){
        return this.pupil;
    }

    @Exclude
    public void setPupil(Pupil pPupil){
        this.pupil = pPupil;
    }

    @Exclude
    public String getFriendlyTimeSlot() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.getDate());
        calendar.add(Calendar.MINUTE, this.getDuration());
        return String.format("%s - %s", DateUtils.getFriendlyHour(this.getDate()),
                DateUtils.getFriendlyHour(calendar.getTime()));
    }

    @Exclude
    public String getFormattedPrice(){
        return StringUtils.formatDouble(this.money);
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(COL_DATE, this.date);
        values.put(COL_DURATION, this.duration);
        values.put(COL_STATE, this.state);
        values.put(COL_MONEY, this.money);
        values.put(COL_CHAPTER, this.chapter);
        values.put(COL_COMMENT, this.comment);
        values.put(COL_UUID, this.uuid);
        values.put(COL_PUPIL_UUID, this.pupilUuid);
        return values;
    }

    @Exclude
    public long getEndDate(){
        return this.date + this.duration * 60 * 1000;
    }

    @Exclude
    public boolean isComplete(){
        if(DateUtils.isPast(date)){
            return !TextUtils.isEmpty(comment) && !TextUtils.isEmpty(chapter);
        } else {
            return true;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return id == course.id &&
                date == course.date &&
                duration == course.duration &&
                state == course.state &&
                Double.compare(course.money, money) == 0 &&
                Objects.equals(chapter, course.chapter) &&
                Objects.equals(comment, course.comment) &&
                Objects.equals(uuid, course.uuid) &&
                Objects.equals(pupilUuid, course.pupilUuid);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", date=" + date +
                ", duration=" + duration +
                ", state=" + state +
                ", money=" + money +
                ", chapter='" + chapter + '\'' +
                ", comment='" + comment + '\'' +
                ", uuid='" + uuid + '\'' +
                ", pupilUuid='" + pupilUuid + '\'' +
                '}';
    }

    @Override
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("date", date);
        result.put("duration", duration);
        result.put("state", state);
        result.put("money", money);
        result.put("chapter", chapter);
        result.put("comment", comment);
        result.put("uuid", uuid);
        result.put("pupilUuid", pupilUuid);
        return result;
    }
}
