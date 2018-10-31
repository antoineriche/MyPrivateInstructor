package com.antoineriche.privateinstructor.beans;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.database.PupilTable;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.antoineriche.privateinstructor.database.PupilTable.COL_CLASS_LEVEL;
import static com.antoineriche.privateinstructor.database.PupilTable.COL_DATE_SINCE;
import static com.antoineriche.privateinstructor.database.PupilTable.COL_FREQUENCY;
import static com.antoineriche.privateinstructor.database.PupilTable.COL_GENDER;
import static com.antoineriche.privateinstructor.database.PupilTable.COL_HOURLY_PRICE;
import static com.antoineriche.privateinstructor.database.PupilTable.COL_IMG_PATH;
import static com.antoineriche.privateinstructor.database.PupilTable.COL_LAST_NAME;
import static com.antoineriche.privateinstructor.database.PupilTable.COL_LOCATION_UUID;
import static com.antoineriche.privateinstructor.database.PupilTable.COL_PARENT_PHONE;
import static com.antoineriche.privateinstructor.database.PupilTable.COL_PAYMENT_TYPE;
import static com.antoineriche.privateinstructor.database.PupilTable.COL_PHONE;
import static com.antoineriche.privateinstructor.database.PupilTable.COL_STATE;
import static com.antoineriche.privateinstructor.database.PupilTable.COL_UUID;

public class Pupil implements Serializable, DatabaseItem {

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
    protected String locationUuid;
    protected Location location;
    protected double hourlyPrice;
    protected long sinceDate;
    protected String phone, parentPhone;
    protected String uuid;
    //Image
    protected String imgPath;

    protected List<Course> mCourses;
    protected List<Devoir> mDevoirs;

    public Pupil() {
    }

    //Use to retrieve and remove malformed data on Firebase
    public Pupil(long pId) {
        this.id = pId;
    }

    public Pupil(String firstname, String lastname, int classLevel, int paymentType, int gender, int frequency, String locationUuid, double hourlyPrice) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.classLevel = classLevel;
        this.paymentType = paymentType;
        this.gender = gender;
        this.frequency = frequency;
        this.hourlyPrice = hourlyPrice;
        this.locationUuid = locationUuid;
    }

    public Pupil(Cursor c) {
        this(c.getString(PupilTable.NUM_COL_FIRST_NAME), c.getString(PupilTable.NUM_COL_LAST_NAME), c.getInt(PupilTable.NUM_COL_CLASS_LEVEL),
                c.getInt(PupilTable.NUM_COL_PAYMENT_TYPE), c.getInt(PupilTable.NUM_COL_GENDER), c.getInt(PupilTable.NUM_COL_FREQUENCY),
                c.getString(PupilTable.NUM_COL_LOCATION_UUID), c.getDouble(PupilTable.NUM_COL_HOURLY_PRICE));

        this.id = c.getLong(PupilTable.NUM_COL_ID);
        this.sinceDate = c.getLong(PupilTable.NUM_COL_DATE_SINCE);
        this.phone = c.getString(PupilTable.NUM_COL_PHONE);
        this.parentPhone = c.getString(PupilTable.NUM_COL_PARENT_PHONE);
        this.imgPath = c.getString(PupilTable.NUM_COL_IMG_PATH);
        this.state = c.getInt(PupilTable.NUM_COL_STATE);
        this.uuid = c.getString(PupilTable.NUM_COL_UUID);
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLocationUuid() {
        return locationUuid;
    }

    public void setLocationUuid(String locationUuid) {
        this.locationUuid = locationUuid;
    }

    @Exclude
    public List<Course> getCourses() {
        return mCourses;
    }

    @Exclude
    public void setCourses(List<Course> pCourses) {
        this.mCourses = pCourses;
    }

    @Exclude
    public List<Devoir> getDevoirs() {
        return mDevoirs;
    }

    @Exclude
    public void setDevoirs(List<Devoir> pDevoirs) {
        this.mDevoirs = pDevoirs;
    }

    @Exclude
    public Location getLocation() {
        return location;
    }

    @Exclude
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
        values.put(COL_HOURLY_PRICE, this.hourlyPrice);
        values.put(COL_DATE_SINCE, this.sinceDate);
        values.put(COL_PHONE, this.phone);
        values.put(COL_PARENT_PHONE, this.parentPhone);
        values.put(COL_IMG_PATH, this.imgPath);
        values.put(COL_STATE, this.state);
        values.put(COL_UUID, this.uuid);
        values.put(COL_LOCATION_UUID, this.locationUuid);
        return values;
    }

    @Exclude
    public String getFullName(){
        return String.format(Locale.FRANCE, "%s %s", this.firstname, this.lastname);
    }

    @Exclude
    public String getFriendlyFrequency(Context context){
        return context.getResources().getStringArray(R.array.pupil_course_frequency)[this.getFrequency()];
    }

    @Exclude
    public String getFriendlyPaymentType(Context context){
        return context.getResources().getStringArray(R.array.pupil_payment_type)[this.getPaymentType()];
    }

    @Exclude
    public String getFriendlyClassLevel(Context context){
        return context.getResources().getStringArray(R.array.pupil_class_levels)[this.getClassLevel()];
    }

    @Exclude
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
                ", hourlyPrice=" + hourlyPrice +
                ", sinceDate=" + sinceDate +
                ", phone='" + phone + '\'' +
                ", parentPhone='" + parentPhone + '\'' +
                ", imgPath='" + imgPath + '\'' +
                ", uuid='" + uuid + '\'' +
                ", locationUuid='" + locationUuid + '\'' +
                '}';
    }

    @Override
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("firstname", firstname);
        result.put("lastname", lastname);
        result.put("classLevel", classLevel);
        result.put("paymentType", paymentType);
        result.put("gender", gender);
        result.put("frequency", frequency);
        result.put("state", state);
        result.put("hourlyPrice", hourlyPrice);
        result.put("sinceDate", sinceDate);
        result.put("phone", phone);
        result.put("parentPhone", parentPhone);
        result.put("imgPath", imgPath);
        result.put("uuid", uuid);
        result.put("locationUuid", locationUuid);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pupil pupil = (Pupil) o;
        return id == pupil.id &&
                classLevel == pupil.classLevel &&
                paymentType == pupil.paymentType &&
                gender == pupil.gender &&
                frequency == pupil.frequency &&
                state == pupil.state &&
                Double.compare(pupil.hourlyPrice, hourlyPrice) == 0 &&
                sinceDate == pupil.sinceDate &&
                Objects.equals(firstname, pupil.firstname) &&
                Objects.equals(lastname, pupil.lastname) &&
                Objects.equals(phone, pupil.phone) &&
                Objects.equals(parentPhone, pupil.parentPhone) &&
                Objects.equals(imgPath, pupil.imgPath) &&
                Objects.equals(uuid, pupil.uuid) &&
                Objects.equals(locationUuid, pupil.locationUuid);
    }
}
