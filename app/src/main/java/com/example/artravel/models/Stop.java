package com.example.artravel.models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

@ParseClassName("Stop")
public class Stop extends ParseObject {
    private static final String KEY_STOP_LOCATION = "stopLocation";
    private static final String KEY_STOP_NAME = "stopName";
    private static final String KEY_STOP_DETAILS = "stopDetails";
    private static final String KEY_GEM= "gem";

    public Stop() {
    }

    public ParseGeoPoint getStopLocation() {
        return getParseGeoPoint(KEY_STOP_LOCATION);
    }

    public String getStopName() {
        return getString(KEY_STOP_NAME);
    }

    public String getStopDetails() {
        return getString(KEY_STOP_DETAILS);
    }

    public Gems getGem() {
        return (Gems) getParseObject(KEY_GEM);
    }



}
