package com.antoineriche.privateinstructor.beans;

public class Pupil {

    public static final int GENDER_MALE = 0;
    public static final int GENDER_FEMALE = 1;

    public static final int REGULAR = 0;
    public static final int OCCASIONALY = 1;
    public static final int TEMPORALY = 2;

    public static final int AGENCY = 0;
    public static final int BLACK = 1;

    public static final int ACTIVE = 0;
    public static final int DESACTIVE = 1;

    protected int id;
    protected String firstname, lastname;
    // classe
    protected int classLevel;
    // black / agence
    protected int paymentType;
    protected int gender;
    protected int frequency;
    protected int state;
    protected String address;
    protected double hourlyPrice;
    protected long sinceDate;
    protected String phone, parentPhone;
    //Image
    protected String imgPath;

    public Pupil() {
    }

    public Pupil(String firstname, String lastname, int classLevel, int paymentType, int gender, int frequency, String address, double hourlyPrice) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.classLevel = classLevel;
        this.paymentType = paymentType;
        this.gender = gender;
        this.frequency = frequency;
        this.address = address;
        this.hourlyPrice = hourlyPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
                ", address='" + address + '\'' +
                ", hourlyPrice=" + hourlyPrice +
                ", sinceDate=" + sinceDate +
                ", phone='" + phone + '\'' +
                ", parentPhone='" + parentPhone + '\'' +
                ", imgPath='" + imgPath + '\'' +
                '}';
    }
}
