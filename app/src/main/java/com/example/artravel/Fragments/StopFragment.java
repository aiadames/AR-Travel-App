package com.example.artravel.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.artravel.Activities.MapsWindowAdapter;
import com.example.artravel.Activities.PathDetailsActivity;
import com.example.artravel.R;
import com.example.artravel.models.Path;
import com.example.artravel.models.Stop;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

import org.parceler.Parcels;

import java.util.ArrayList;


import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

@RuntimePermissions
public class StopFragment extends Fragment {

    private Path path;
    private ArrayList<Stop> stopsList;
    private int stopIndex;
    private Stop currentStop;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    Location mCurrentLocation;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    private final static String KEY_LOCATION = "location";

    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private TextView tvStopName;
    private TextView tvStopDetails;
    private TextView tvPathName;
    private Button btnNextStop;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();
        path = Parcels.unwrap(bundle.getParcelable("Path"));
        stopsList = Parcels.unwrap(bundle.getParcelable("Stops Array"));
        stopIndex = bundle.getInt("Stop Index");
        currentStop = stopsList.get(stopIndex);

        tvStopName = view.findViewById(R.id.tvStopName);
        tvStopDetails = view.findViewById(R.id.tvStopDetails);
        tvPathName = view.findViewById(R.id.tvPathName);
        btnNextStop = view.findViewById(R.id.btnNextStop);

        tvStopName.setText(currentStop.getStopName());
        tvStopDetails.setText(currentStop.getStopDetails());
        tvPathName.setText(path.getPathName());


//        for (int i = 0; i < stopsList.size(); i++) {
//            Log.d("StopFragment", stopsList.get(i).getStopName());
//        }

        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }

        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    loadMap(map);
                    map.setInfoWindowAdapter(new MapsWindowAdapter(getLayoutInflater()));

                    for (int i = 0; i < stopsList.size(); i++) {
                        createStopMarker(stopsList.get(i));
                    }

                    //createTestMarker();
                }
            });
        } else {
            Toast.makeText(getContext(), "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }

        btnNextStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment stopFragment = new StopFragment();

                Bundle bundle = new Bundle();
                bundle.putParcelable("Path", Parcels.wrap(path));
                bundle.putParcelable("Stops Array", Parcels.wrap(stopsList));
                if (stopIndex < 4) {
                    stopIndex++;
                }
                bundle.putInt("Stop Index", stopIndex);
                stopFragment.setArguments(bundle);

                FragmentManager fragmentManager = ((AppCompatActivity)getActivity()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContainer, stopFragment)
                        .commit();
            }
        });



    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            StopFragmentPermissionsDispatcher.getMyLocationWithPermissionCheck(this);
            StopFragmentPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);

            ParseGeoPoint stopLocation = null;
            try {
                stopLocation = currentStop.fetchIfNeeded().getParseGeoPoint("stopLocation");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            LatLng latLng = new LatLng(stopLocation.getLatitude(), stopLocation.getLongitude());
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
//            map.animateCamera(cameraUpdate);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17.5f));

        } else {
            Toast.makeText(getContext(), "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }



    // Fires when a long press happens on the map
//    @Override
//    public void onMapLongClick(final LatLng point) {
//        Toast.makeText(this, "Long Press", Toast.LENGTH_LONG).show();
////        // Custom code here...
//        showAlertDialogForPoint(point);
//    }

//    private void showAlertDialogForPoint(final LatLng point) {
//        // inflate message_item.xml view
//        View messageView = LayoutInflater.from(PathDetailsActivity.this).
//                inflate(R.layout.message_item, null);
//        // Create alert dialog builder
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        // set message_item.xml to AlertDialog builder
//        alertDialogBuilder.setView(messageView);
//
//        // Create alert dialog
//        final AlertDialog alertDialog = alertDialogBuilder.create();
//
//        // Configure dialog button (OK)
//        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Define color of marker icon
//                        BitmapDescriptor defaultMarker =
//                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
//                        // Extract content from alert dialog
//                        String title = ((EditText) alertDialog.findViewById(R.id.etTitle)).
//                                getText().toString();
//                        String snippet = ((EditText) alertDialog.findViewById(R.id.etSnippet)).
//                                getText().toString();
//                        // Creates and adds marker to the map
//                        Marker marker = map.addMarker(new MarkerOptions()
//                                .position(point)
//                                .title(title)
//                                .snippet(snippet)
//                                .icon(defaultMarker));
//                    }
//                });
//
//        // Configure dialog button (Cancel)
//        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) { dialog.cancel(); }
//                });
//
//        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Define color of marker icon
//                        BitmapDescriptor defaultMarker = BitmapDescriptorFactory
//                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
//                        // Extract content from alert dialog
//                        String title = ((EditText) alertDialog.findViewById(R.id.etTitle))
//                                .getText().toString();
//                        String snippet = ((EditText) alertDialog.findViewById(R.id.etSnippet))
//                                .getText().toString();
//                        // Creates and adds marker to the map
//                        Marker marker = map.addMarker(new MarkerOptions().position(point)
//                                .title(title).snippet(snippet).icon(defaultMarker));
//
//                        // Animate marker using drop effect
//                        // --> Call the dropPinEffect method here
//                        dropPinEffect(marker);
//                    }
//                });
//
//        // Display the dialog
//        alertDialog.show();
//    }

//    private void dropPinEffect(final Marker marker) {
//        // Handler allows us to repeat a code block after a specified delay
//        final android.os.Handler handler = new android.os.Handler();
//        final long start = SystemClock.uptimeMillis();
//        final long duration = 1500;
//
//        // Use the bounce interpolator
//        final android.view.animation.Interpolator interpolator =
//                new BounceInterpolator();
//
//        // Animate marker with a bounce updating its position every 15ms
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                long elapsed = SystemClock.uptimeMillis() - start;
//                // Calculate t for bounce based on elapsed time
//                float t = Math.max(
//                        1 - interpolator.getInterpolation((float) elapsed
//                                / duration), 0);
//                // Set the anchor
//                marker.setAnchor(0.5f, 1.0f + 14 * t);
//
//                if (t > 0.0) {
//                    // Post this event again 15ms from now.
//                    handler.postDelayed(this, 15);
//                } else { // done elapsing, show window
//                    marker.showInfoWindow();
//                }
//            }
//        });
//    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        StopFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @SuppressWarnings({"MissingPermission"})
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getMyLocation() {
        map.setMyLocationEnabled(true);

        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(getContext());
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            //onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    /*
     * Called when the Activity becomes visible.
     */
    @Override
    public void onStart() {
        super.onStart();
    }

    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    public void onStop() {
        super.onStop();
    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                PathDetailsActivity.ErrorDialogFragment errorFragment = new PathDetailsActivity.ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getChildFragmentManager(), "Location Updates");
            }

            return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Display the connection status

        if (mCurrentLocation != null) {
            Toast.makeText(getContext(), "GPS location was found!", Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            map.animateCamera(cameraUpdate);
        } else {
            //Toast.makeText(getContext(), "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
        }
        StopFragmentPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(getContext());
        settingsClient.checkLocationSettings(locationSettingsRequest);
        //noinspection MissingPermission
        getFusedLocationProviderClient(getContext()).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        //onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

//    public void onLocationChanged(Location location) {
//        // GPS may be turned off
//        if (location == null) {
//            return;
//        }
//
//        // Report to the UI that the location was updated
//
//        mCurrentLocation = location;
//        String msg = "Updated Location: " +
//                Double.toString(location.getLatitude()) + "," +
//                Double.toString(location.getLongitude());
//        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
//    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    private void createStopMarker(Stop stop) {
        ParseGeoPoint stopLocation = stop.getStopLocation();

        Marker mapMarker = map.addMarker(new MarkerOptions()
                .position(new LatLng(stopLocation.getLatitude(), stopLocation.getLongitude()))
                .title(stop.getStopName())
                .snippet(stop.getStopDetails())
        );


        Circle mapCircle = map.addCircle(new CircleOptions()
                .center(new LatLng(stopLocation.getLatitude(), stopLocation.getLongitude()))
                .radius(30)
                .strokeColor(Color.RED)
                .fillColor(0x55FF0000)
                .strokeWidth(4));

        if (stop.equals(currentStop)) {
            BitmapDescriptor stopIcon =
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
            mapMarker.setIcon(stopIcon);
            mapCircle.setFillColor(0x35ff00ff );
            mapCircle.setStrokeColor(Color.MAGENTA);
        }

    }



}
