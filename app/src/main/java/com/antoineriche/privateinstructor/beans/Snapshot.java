package com.antoineriche.privateinstructor.beans;

import com.antoineriche.privateinstructor.utils.SnapshotFactory;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Snapshot implements Serializable {

    private Date mDate;
    private String mName;
    private List<Course> mCourses;
    private List<Pupil> mPupils;
    private List<Location> mLocations;

    public Snapshot(Date mDate, List<Course> pCourses, List<Pupil> pPupils, List<Location> pLocations) {
        this.mName = SnapshotFactory.createSnapshotName(mDate);
        this.mDate = mDate;
        this.mCourses = pCourses;
        this.mPupils = pPupils;
        this.mLocations = pLocations;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public List<Course> getCourses() {
        return mCourses;
    }

    public void setCourses(List<Course> pCourses) {
        this.mCourses = pCourses;
    }

    public List<Pupil> getPupils() {
        return mPupils;
    }

    public void setPupils(List<Pupil> pPupils) {
        this.mPupils = pPupils;
    }

    public List<Location> getLocations() {
        return mLocations;
    }

    public void setLocations(List<Location> pLocations) {
        this.mLocations = pLocations;
    }
}
