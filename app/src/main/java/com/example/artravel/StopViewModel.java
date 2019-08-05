package com.example.artravel;

import android.location.Location;

import com.example.artravel.models.Path;
import com.example.artravel.models.Stop;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class StopViewModel {

    private static final float ZOOM_LEVEL = 17.5f;

    private Stop stop;
    private Path path;
    private GoogleMap map;

    private double stopLatitude;
    private double stopLongitude;

    public void setStop(Stop stop) {
        this.stop = stop;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public void setStopLatitude(double latitude) {
        stopLatitude = latitude;
    }

    public void setStopLongitude(double longitude) {
        stopLongitude = longitude;
    }

    public void setMap(GoogleMap map) {
        this.map = map;
    }

    public String getStopName() {
        return stop.getStopName();
    }

    public String getStopDetails() {
        return stop.getStopDetails();
    }

    public String getPathName() {
        return path.getPathName();
    }


    public void onClickStopZoom() {
        LatLng latLng = new LatLng(stopLatitude, stopLongitude);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL));
    }

}
