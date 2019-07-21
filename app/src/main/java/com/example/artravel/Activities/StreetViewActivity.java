package com.example.artravel.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;

import com.example.artravel.R;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;

public class StreetViewActivity extends AppCompatActivity implements OnStreetViewPanoramaReadyCallback {

    private StreetViewPanorama panorama;
    StreetViewPanoramaFragment panoramaFragment;
    private StreetViewPanorama.OnStreetViewPanoramaClickListener streetViewPanoramaClickListener;
    private StreetViewPanorama.OnStreetViewPanoramaChangeListener streetViewPanoramaChangeListener;

    private double stopLatitude;
    private double stopLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);

        stopLatitude = getIntent().getDoubleExtra("Stop Latitude", 0);
        stopLongitude = getIntent().getDoubleExtra("Stop Longitude", 0);

        panoramaFragment= (StreetViewPanoramaFragment) getFragmentManager()
                .findFragmentById(R.id.streetViewMap);
        panoramaFragment.getStreetViewPanoramaAsync(this);

        streetViewPanoramaClickListener = new StreetViewPanorama.OnStreetViewPanoramaClickListener() {
            @Override
            public void onStreetViewPanoramaClick(StreetViewPanoramaOrientation streetViewPanoramaOrientation) {
                Point point = panorama.orientationToPoint(streetViewPanoramaOrientation);
                if (point != null) {
                    panorama.animateTo(
                            new StreetViewPanoramaCamera.Builder()
                                    .orientation(streetViewPanoramaOrientation)
                                    .zoom(panorama.getPanoramaCamera().zoom)
                                    .build(), 1000);
                }
            }
        };

        streetViewPanoramaChangeListener = new StreetViewPanorama.OnStreetViewPanoramaChangeListener() {
            @Override
            public void onStreetViewPanoramaChange(StreetViewPanoramaLocation streetViewPanoramaLocation) {
                Log.e("StreetViewActivity", "Street View Panorama Change Listener");
            }
        };
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        panorama = streetViewPanorama;
        streetViewPanorama.setPosition(new LatLng(stopLatitude, stopLongitude));
        streetViewPanorama.setOnStreetViewPanoramaChangeListener(streetViewPanoramaChangeListener);
        streetViewPanorama.setOnStreetViewPanoramaClickListener(streetViewPanoramaClickListener);
        panorama.setStreetNamesEnabled(true);
        panorama.setPanningGesturesEnabled(true);
    }
}

