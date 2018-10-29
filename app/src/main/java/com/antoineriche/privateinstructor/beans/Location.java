package com.antoineriche.privateinstructor.beans;

import android.content.ContentValues;
import android.database.Cursor;

import com.antoineriche.privateinstructor.database.LocationTable;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.antoineriche.privateinstructor.database.LocationTable.COL_ADDRESS;
import static com.antoineriche.privateinstructor.database.LocationTable.COL_LATITUDE;
import static com.antoineriche.privateinstructor.database.LocationTable.COL_LONGITUDE;
import static com.antoineriche.privateinstructor.database.LocationTable.COL_UUID;

public class Location implements Serializable, DatabaseItem {

    private long id;
    private String address;
    private double latitude, longitude;
    private String uuid;

    public Location() {
    }

    //Use to retrieve and remove malformed data on Firebase
    public Location(long pId) {
        this.id = pId;
    }

    public Location(String address, double latitude, double longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location(long id, String address, double latitude, double longitude) {
        this(address, latitude, longitude);
        this.id = id;
    }

    public Location(Cursor c) {
        this(c.getLong(LocationTable.NUM_COL_ID), c.getString(LocationTable.NUM_COL_ADDRESS),
                c.getDouble(LocationTable.NUM_COL_LATITUDE), c.getDouble(LocationTable.NUM_COL_LONGITUDE));
        this.uuid = c.getString(LocationTable.NUM_COL_UUID);
    }

    public Location(Place mPlace) {
        this.address = mPlace.getAddress().toString();
        this.latitude = mPlace.getLatLng().latitude;
        this.longitude = mPlace.getLatLng().longitude;
        this.uuid = UUID.randomUUID().toString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public LatLng getLatLng(){
        return new LatLng(this.latitude, this.longitude);
    }

    public static boolean areEqual(Location l1, Location l2) {
        return l1 != null && l2 != null
                && l1.getLongitude() == l2.getLongitude()
                && l1.getLatitude() == l2.getLatitude();
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(COL_ADDRESS, this.address);
        values.put(COL_LATITUDE, this.latitude);
        values.put(COL_LONGITUDE, this.longitude);
        values.put(COL_UUID, this.uuid);
        return values;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", uuid=" + uuid +
                '}';
    }

    @Override
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("address", address);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("uuid", uuid);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return id == location.id &&
                Double.compare(location.latitude, latitude) == 0 &&
                Double.compare(location.longitude, longitude) == 0 &&
                Objects.equals(address, location.address) &&
                Objects.equals(uuid, location.uuid);
    }
}
