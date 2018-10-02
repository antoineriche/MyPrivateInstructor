package com.antoineriche.privateinstructor.beans;

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
    protected int id;
    protected int pupilID;
    protected long date;
    protected int duration;
    protected int state;
    protected double money;
    protected String chapter;
    protected String comment;

    public Course(){}

    public Course(long date, int duration, int state, double money) {
        this.date = date;
        this.duration = duration;
        this.state = state;
        this.money = money;
    }

    public Course(long date, int duration, int state, double money, String chapter, String comment, int pupilId) {
        this(date, duration, state, money);
        this.chapter = chapter;
        this.comment = comment;
    }

    public Course(int id, int pupilID, long date, int duration, int state, double money, String chapter, String comment) {
        this(date, duration, state, money, chapter, comment, pupilID);
        this.id = id;
        this.pupilID = pupilID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPupilID() {
        return pupilID;
    }

    public void setPupilID(int pupilID) {
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
