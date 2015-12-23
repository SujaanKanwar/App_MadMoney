package com.payment.sujan.madmoney.AppData;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Sujan on 12/19/2015.
 */
public class Position implements Serializable {
    private String id;
    private String latitude;
    private String longitude;
    private String radius;

    public Position() {
    }

    public Position(String id, String latitude, String longitude, String radius) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
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

}
