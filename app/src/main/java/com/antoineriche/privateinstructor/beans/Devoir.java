package com.antoineriche.privateinstructor.beans;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.database.DevoirTable;
import com.antoineriche.privateinstructor.utils.DateUtils;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.antoineriche.privateinstructor.database.DevoirTable.COL_DATE;
import static com.antoineriche.privateinstructor.database.DevoirTable.COL_CHAPTER;
import static com.antoineriche.privateinstructor.database.DevoirTable.COL_COMMENT;
import static com.antoineriche.privateinstructor.database.DevoirTable.COL_DURATION;
import static com.antoineriche.privateinstructor.database.DevoirTable.COL_MARK;
import static com.antoineriche.privateinstructor.database.DevoirTable.COL_MAX_MARK;
import static com.antoineriche.privateinstructor.database.DevoirTable.COL_PUPIL_UUID;
import static com.antoineriche.privateinstructor.database.DevoirTable.COL_STATE;
import static com.antoineriche.privateinstructor.database.DevoirTable.COL_TYPE;
import static com.antoineriche.privateinstructor.database.DevoirTable.COL_UUID;


public class Devoir implements Serializable, DatabaseItem, EventItem {

    //Statics

    public static final int DST = 0;
    public static final int DM = 1;
    public static final int INTERROGATION = 2;

    protected long id;
    protected String pupilUuid;
    protected long date;
    protected double mark, maxMark;
    protected String chapter;           //
    protected String comment;           //
    protected int state, type;          //
    protected int duration;          //
    protected String uuid;              //
    protected Pupil pupil;              //

    public Devoir(){
    }

    //Use to retrieve and remove malformed data on Firebase
    public Devoir(long id){
        this.id = id;
    }

    public Devoir(long id, String pupilUuid, long date, double mark, double maxMark, String chapter, String comment, int state, int type, String uuid, int duration) {
        this.id = id;
        this.pupilUuid = pupilUuid;
        this.date = date;
        this.mark = mark;
        this.maxMark = maxMark;
        this.chapter = chapter;
        this.comment = comment;
        this.state = state;
        this.type = type;
        this.uuid = uuid;
        this.duration = duration;
    }

    public Devoir(Cursor c) {
        this(c.getLong(DevoirTable.NUM_COL_ID), c.getString(DevoirTable.NUM_COL_PUPIL_UUID),
                c.getLong(DevoirTable.NUM_COL_DATE), c.getInt(DevoirTable.NUM_COL_MARK),
                c.getInt(DevoirTable.NUM_COL_MAX_MARK), c.getString(DevoirTable.NUM_COL_CHAPTER),
                c.getString(DevoirTable.NUM_COL_COMMENT), c.getInt(DevoirTable.NUM_COL_STATE),
                c.getInt(DevoirTable.NUM_COL_TYPE), c.getString(DevoirTable.NUM_COL_UUID),
                c.getInt(DevoirTable.NUM_COL_DURATION));
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPupilUuid() {
        return pupilUuid;
    }

    public void setPupilUuid(String pupilUuid) {
        this.pupilUuid = pupilUuid;
    }

    @Override
    public long getDate() {
        return date;
    }

    @Override
    public void setDate(long date) {
        this.date = date;
    }

    public double getMark() {
        return mark;
    }

    public void setMark(double mark) {
        this.mark = mark;
    }

    public double getMaxMark() {
        return maxMark;
    }

    public void setMaxMark(double maxMark) {
        this.maxMark = maxMark;
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
    public int getState() {
        return state;
    }

    @Override
    public void setState(int state) {
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Exclude
    public Pupil getPupil() {
        return pupil;
    }

    @Exclude
    public void setPupil(Pupil pupil) {
        this.pupil = pupil;
    }

    @Exclude
    public String getFriendlyType(Context context){
        return context.getResources().getStringArray(R.array.devoir_types)[this.getType()];
    }

    @Exclude
    public String getFriendlyDuration(){
        return DateUtils.formatRemainingTime(1000 * 60 * this.duration);
    }

    @Override
    public String toString() {
        return "Devoir{" +
                "id=" + id +
                ", pupilUuid='" + pupilUuid + '\'' +
                ", date=" + date +
                ", mark=" + mark +
                ", maxMark=" + maxMark +
                ", chapter='" + chapter + '\'' +
                ", comment='" + comment + '\'' +
                ", state=" + state +
                ", type=" + type +
                ", duration=" + duration +
                ", uuid='" + uuid + '\'' +
                '}';
    }

    @Override
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("pupilUuid", pupilUuid);
        result.put("date", date);
        result.put("mark", mark);
        result.put("maxMark", maxMark);
        result.put("chapter", chapter);
        result.put("comment", comment);
        result.put("state", state);
        result.put("type", type);
        result.put("duration", duration);
        result.put("uuid", uuid);
        return result;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(COL_PUPIL_UUID, this.pupilUuid);
        values.put(COL_DATE, this.date);
        values.put(COL_MARK, this.mark);
        values.put(COL_MAX_MARK, this.maxMark);
        values.put(COL_CHAPTER, this.chapter);
        values.put(COL_COMMENT, this.comment);
        values.put(COL_STATE, this.state);
        values.put(COL_TYPE, this.type);
        values.put(COL_UUID, this.uuid);
        values.put(COL_DURATION, this.duration);
        values.put(COL_PUPIL_UUID, this.pupilUuid);
        return values;
    }
}
