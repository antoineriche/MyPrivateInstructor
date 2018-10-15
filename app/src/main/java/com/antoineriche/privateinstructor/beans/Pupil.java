package com.antoineriche.privateinstructor.beans;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.database.PupilTable;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import static com.antoineriche.privateinstructor.database.PupilTable.COL_CLASS_LEVEL;
import static com.antoineriche.privateinstructor.database.PupilTable.COL_DATE_SINCE;
import static com.antoineriche.privateinstructor.database.PupilTable.COL_FREQUENCY;
import static com.antoineriche.privateinstructor.database.PupilTable.COL_GENDER;
import static com.antoineriche.privateinstructor.database.PupilTable.COL_HOURLY_PRICE;
import static com.antoineriche.privateinstructor.database.PupilTable.COL_IMG_PATH;
import static com.antoineriche.privateinstructor.database.PupilTable.COL_LAST_NAME;
import static com.antoineriche.privateinstructor.database.PupilTable.COL_LOCATION_ID;
import static com.antoineriche.privateinstructor.database.PupilTable.COL_PARENT_PHONE;
import static com.antoineriche.privateinstructor.database.PupilTable.COL_PAYMENT_TYPE;
import static com.antoineriche.privateinstructor.database.PupilTable.COL_PHONE;
import static com.antoineriche.privateinstructor.database.PupilTable.COL_STATE;

public class Pupil implements Serializable {

    public static final int GENDER_MALE = 0;
    public static final int GENDER_FEMALE = 1;

    public static final int REGULAR = 0;
    public static final int OCCASIONALLY = 1;
    public static final int TEMPORARILY = 2;

    public static final int AGENCY = 0;
    public static final int BLACK = 1;

    public static final int ACTIVE = 0;
    public static final int DESACTIVE = 1;

    protected long id;
    protected String firstname, lastname;
    // classe
    protected int classLevel;
    // black / agence
    protected int paymentType;
    protected int gender;
    protected int frequency;
    protected int state;
    protected long locationId;
    protected Location location;
    protected double hourlyPrice;
    protected long sinceDate;
    protected String phone, parentPhone;
    //Image
    protected String imgPath;

    protected List<Course> mCourses;

    public Pupil() {
    }

    public Pupil(String firstname, String lastname, int classLevel, int paymentType, int gender, int frequency, long locationId, double hourlyPrice) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.classLevel = classLevel;
        this.paymentType = paymentType;
        this.gender = gender;
        this.frequency = frequency;
        this.hourlyPrice = hourlyPrice;
        this.locationId = locationId;
    }

    public Pupil(Cursor c) {
        this(c.getString(PupilTable.NUM_COL_FIRST_NAME), c.getString(PupilTable.NUM_COL_LAST_NAME), c.getInt(PupilTable.NUM_COL_CLASS_LEVEL),
                c.getInt(PupilTable.NUM_COL_PAYMENT_TYPE), c.getInt(PupilTable.NUM_COL_GENDER), c.getInt(PupilTable.NUM_COL_FREQUENCY),
                c.getLong(PupilTable.NUM_COL_LOCATION_ID), c.getDouble(PupilTable.NUM_COL_HOURLY_PRICE));

        this.id = c.getLong(PupilTable.NUM_COL_ID);
        this.sinceDate = c.getLong(PupilTable.NUM_COL_DATE_SINCE);
        this.phone = c.getString(PupilTable.NUM_COL_PHONE);
        this.parentPhone = c.getString(PupilTable.NUM_COL_PARENT_PHONE);
        this.imgPath = c.getString(PupilTable.NUM_COL_IMG_PATH);
        this.state = c.getInt(PupilTable.NUM_COL_STATE);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getClassLevel() {
        return classLevel;
    }

    public void setClassLevel(int classLevel) {
        this.classLevel = classLevel;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long pLocationId) {
        this.locationId = pLocationId;
    }

    public double getHourlyPrice() {
        return hourlyPrice;
    }

    public void setHourlyPrice(double hourlyPrice) {
        this.hourlyPrice = hourlyPrice;
    }

    public long getSinceDate() {
        return sinceDate;
    }

    public void setSinceDate(long sinceDate) {
        this.sinceDate = sinceDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getParentPhone() {
        return parentPhone;
    }

    public void setParentPhone(String parentPhone) {
        this.parentPhone = parentPhone;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public List<Course> getCourses() {
        return mCourses;
    }

    public void setCourses(List<Course> pCourses) {
        this.mCourses = pCourses;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ContentValues toContentValues(){
        ContentValues values = new ContentValues();
        values.put(PupilTable.COL_FIRST_NAME, this.firstname);
        values.put(COL_LAST_NAME, this.lastname);
        values.put(COL_GENDER, this.gender);
        values.put(COL_CLASS_LEVEL, this.classLevel);
        values.put(COL_PAYMENT_TYPE, this.paymentType);
        values.put(COL_FREQUENCY, this.frequency);
        values.put(COL_LOCATION_ID, this.locationId);
        values.put(COL_HOURLY_PRICE, this.hourlyPrice);
        values.put(COL_DATE_SINCE, this.sinceDate);
        values.put(COL_PHONE, this.phone);
        values.put(COL_PARENT_PHONE, this.parentPhone);
        values.put(COL_IMG_PATH, this.imgPath);
        values.put(COL_STATE, this.state);
        return values;
    }

    public String getFullName(){
        return String.format(Locale.FRANCE, "%s %s", this.firstname, this.lastname);
    }

    public String getFriendlyFrequency(Context context){
        return context.getResources().getStringArray(R.array.pupil_course_frequency)[this.getFrequency()];
    }

    public String getFriendlyPaymentType(Context context){
        return context.getResources().getStringArray(R.array.pupil_payment_type)[this.getPaymentType()];
    }

    public String getFriendlyClassLevel(Context context){
        return context.getResources().getStringArray(R.array.pupil_class_levels)[this.getClassLevel()];
    }

    public String getFriendlyHourlyPrice(){
        return String.format(Locale.FRANCE, "%.02f", this.getHourlyPrice());
    }

    @Override
    public String toString() {
        return "Pupil{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", classLevel=" + classLevel +
                ", paymentType=" + paymentType +
                ", gender=" + gender +
                ", frequency=" + frequency +
                ", state=" + state +
                ", locationId=" + locationId +
                ", hourlyPrice=" + hourlyPrice +
                ", sinceDate=" + sinceDate +
                ", phone='" + phone + '\'' +
                ", parentPhone='" + parentPhone + '\'' +
                ", imgPath='" + imgPath + '\'' +
                '}';
    }
}
