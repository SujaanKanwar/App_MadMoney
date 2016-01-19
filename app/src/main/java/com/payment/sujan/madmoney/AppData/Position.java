package com.payment.sujan.madmoney.AppData;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Sujan on 12/19/2015.
 */
public class Position implements Serializable {
    private int id;
    private String locationName;
    private String latitude;
    private String longitude;
    private String radius;
    private String city;
    private String description;
    private String requestId;
    private int loiteringTime;
    private String geoTransactionType;
    private int expirationTime;
    private String dateAndTimeOfDiscover;

    public Position() {
    }

    public Position(int id, String locationName, String latitude, String longitude, String radius
    ,String city, String description, String requestId, int loiteringTime,
                    String geoTransactionType, int expirationTime, String dateAndTimeOfDiscover) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.locationName = locationName;
        this.city = city;
        this.description =description;
        this.requestId = requestId;
        this.loiteringTime = loiteringTime;
        this.geoTransactionType = geoTransactionType;
        this.expirationTime = expirationTime;
        this.dateAndTimeOfDiscover = dateAndTimeOfDiscover;
    }

    public int getId() {
        return id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getLoiteringTime() {
        return loiteringTime;
    }

    public void setLoiteringTime(int loiteringTime) {
        this.loiteringTime = loiteringTime;
    }

    public String getGeoTransactionType() {
        return geoTransactionType;
    }

    public void setGeoTransactionType(String geoTransactionType) {
        this.geoTransactionType = geoTransactionType;
    }

    public int getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(int expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getDateAndTimeOfDiscover() {
        return dateAndTimeOfDiscover;
    }
}
