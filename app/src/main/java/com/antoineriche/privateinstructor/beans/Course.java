package com.antoineriche.privateinstructor.beans;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.utils.DateUtils;
import com.antoineriche.privateinstructor.utils.StringUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.antoineriche.privateinstructor.database.CourseTable.COL_CHAPTER;
import static com.antoineriche.privateinstructor.database.CourseTable.COL_COMMENT;
import static com.antoineriche.privateinstructor.database.CourseTable.COL_DATE;
import static com.antoineriche.privateinstructor.database.CourseTable.COL_DURATION;
import static com.antoineriche.privateinstructor.database.CourseTable.COL_MONEY;
import static com.antoineriche.privateinstructor.database.CourseTable.COL_PUPIL_ID;
import static com.antoineriche.privateinstructor.database.CourseTable.COL_STATE;

public class Course implements Serializable {

    //Statics
    public static final int FORESEEN = 0;
    public static final int VALIDATED = 1;
    public static final int WAITING_FOT_VALIDATION = 2;
    public static final int CANCELED = 3;

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

    public String getFriendlyTimeSlot() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH'h'mm", Locale.FRANCE);
        String stHour = sdf.format(this.getDate());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.getDate());
        calendar.add(Calendar.MINUTE, this.getDuration());
        return String.format("%s - %s", stHour, sdf.format(calendar.getTime()));
    }

    public String getFriendlyDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.FRANCE);
        return StringUtils.capitalizeFirstChar(sdf.format(this.getDate()));
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

    public Drawable getStateIcon(Context context){
        Drawable drawable;
        switch(this.getState()){
            case FORESEEN:
                drawable = context.getDrawable(R.drawable.baseline_schedule_white_48);
                drawable.setTint(context.getColor(R.color.themPrimaryDark));
                break;
            case WAITING_FOT_VALIDATION:
                drawable = context.getDrawable(R.drawable.baseline_hourglass_empty_white_48);
                drawable.setTint(context.getColor(R.color.themPrimaryDark));
                break;
            case VALIDATED:
                drawable = context.getDrawable(R.drawable.baseline_check_circle_white_48);
                drawable.setTint(context.getColor(R.color.green500));
                break;
            case CANCELED:
                drawable = context.getDrawable(R.drawable.baseline_highlight_off_white_48);
                drawable.setTint(context.getColor(R.color.red500));
                break;
            default:
                drawable = context.getDrawable(R.drawable.baseline_hourglass_empty_white_48);
                drawable.setTint(context.getColor(R.color.themPrimaryDark));
                break;
        }
        return drawable;
    }

    public boolean isTheGoodDay(Date pDate){
        return isBetweenDates(DateUtils.getFirstSecond(pDate), DateUtils.getLastSecond(pDate));
    }

    public boolean isBetweenDates(Date pStartDate, Date pEndDate){
        Date courseDate = new Date(this.date);
        return courseDate.after(pStartDate) && courseDate.before(pEndDate);
    }

    public String getFriendlyStatus(Context context){
        return context.getResources().getStringArray(R.array.course_states)[this.getState()];
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
