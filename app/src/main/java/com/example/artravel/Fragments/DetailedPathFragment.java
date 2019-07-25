package com.example.artravel.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artravel.Activities.MapsWindowAdapter;
import com.example.artravel.Activities.PathDetailsActivity;
import com.example.artravel.R;
import com.example.artravel.StopsAdapter;
import com.example.artravel.StopsItemTouchHelperCallback;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

@RuntimePermissions
public class DetailedPathFragment extends Fragment {

    private ArrayList<Stop> stops;
    private Stop stop1;
    private double stop1Latitude;
    private double stop1Longitude;

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    Location mCurrentLocation;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    private LocationCallback mLocationCallback;

    private TextView tvPathName;
    private TextView tvPathDescription;
    private RatingBar rbPathRating;
    private Button btnStartPath;
    private Path currentPath;

    private final static String KEY_LOCATION = "location";
    private static final int MARKER_HEIGHT = 100;
    private static final int MARKER_WIDTH = 100;
    private static final int STOP_RADIUS = 30;
    private static final float ZOOM_LEVEL = 13.0f;

    boolean inProgress = false;

    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detailed_path, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvPathName = view.findViewById(R.id.tvPathName);
        tvPathDescription = view.findViewById(R.id.tvPathDescription);
        rbPathRating = view.findViewById(R.id.rbPathRating);
        btnStartPath = view.findViewById(R.id.btnStartPath);
        RecyclerView rvStops = view.findViewById(R.id.rvStops);

        Bundle bundle = this.getArguments();
        currentPath = Parcels.unwrap(bundle.getParcelable("Path"));

        initializeViews();


        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            ParseRelation<Path> relation = currentUser.getRelation("startedPaths");
            relation.getQuery().findInBackground(new FindCallback<Path>() {
                @Override
                public void done(List<Path> objects, ParseException e) {
                    if (e != null) {
                        e.printStackTrace();
                    } else {
                        Log.e("DetailedPathFragment", "Success!");
                        for (int i = 0; i < objects.size(); i++) {
                            if (objects.get(i).getObjectId().equals(currentPath.getObjectId())) {
                                btnStartPath.setText("Resume path");
                                inProgress = true;
                            }
                        }
                    }
                }
            });
        }

        stops = createStopsList();
//        stop1 = currentPath.getStop1();
//        ParseGeoPoint stop1Location = getLocationOfStop1();
//        stop1Latitude = stop1Location.getLatitude();
//        stop1Longitude = stop1Location.getLongitude();


        // REPLACE THIS CODE WITH CODE BELOW IF YOU WANT ALL STOPS TO SHOW - OTHERWISE SHOWS ONLY STOPS THAT HAVEN'T BEEN VISITED
        // Can modify this code to display already visited stops differently
        ArrayList<Stop> newStops = new ArrayList<>();
        newStops.addAll(stops);
        if (currentUser != null) {
            ParseRelation<Stop> relation = currentUser.getRelation("visitedStops");
            relation.getQuery().findInBackground(new FindCallback<Stop>() {
                @Override
                public void done(List<Stop> objects, ParseException e) {
                    if (e != null) {
                        e.printStackTrace();
                    } else {
                        for (int i = 0; i < stops.size(); i++) {
                            for (int j = 0; j < objects.size(); j++) {
                                if (stops.get(i).getObjectId().equals(objects.get(j).getObjectId())) {
                                    newStops.remove(stops.get(i));
                                }
                            }
                        }
                    }

                    stops = newStops;
                    if (stops.size() >= 1) {
                        stop1 = stops.get(0);
                    }
                    else {
                        stop1 = currentPath.getStop1();
                    }
                    ParseGeoPoint stop1Location = getLocationOfStop(stop1);
                    stop1Latitude = stop1Location.getLatitude();
                    stop1Longitude = stop1Location.getLongitude();

                    StopsAdapter adapter = new StopsAdapter(stops, getContext());
                    rvStops.setAdapter(adapter);
                    rvStops.setLayoutManager(new LinearLayoutManager(getContext()));

                    ItemTouchHelper.Callback callback =
                            new StopsItemTouchHelperCallback(adapter);
                    ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                    touchHelper.attachToRecyclerView(rvStops);

                    setUpMapFragment(savedInstanceState);
                }
            });
        }
//
//        stops = newStops;
//
//
//        StopsAdapter adapter = new StopsAdapter(stops, getContext());
//        rvStops.setAdapter(adapter);
//        rvStops.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        ItemTouchHelper.Callback callback =
//                new StopsItemTouchHelperCallback(adapter);
//        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
//        touchHelper.attachToRecyclerView(rvStops);
//        setUpMapFragment(savedInstanceState);

        btnStartPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    ParseRelation<Path> relation = currentUser.getRelation("startedPaths");
                    relation.add(currentPath);
                    currentUser.saveInBackground();
                }

                Fragment stopFragment = new StopFragment();

                Bundle bundle = new Bundle();
                bundle.putParcelable("Path", Parcels.wrap(currentPath));
                bundle.putParcelable("Stops Array", Parcels.wrap(stops));
                bundle.putInt("Stop Index", 0);
                stopFragment.setArguments(bundle);

                FragmentManager fragmentManager = ((AppCompatActivity)getActivity()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContainer, stopFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack("Path Detail").commit();
            }
        });
    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            DetailedPathFragmentPermissionsDispatcher.getMyLocationWithPermissionCheck(this);
            DetailedPathFragmentPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);

            LatLng latLng = new LatLng(stop1Latitude, stop1Longitude);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,ZOOM_LEVEL));

        } else {
            Toast.makeText(getContext(), "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        DetailedPathFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
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
        startLocationUpdates();

        // Display the connection status

        if (mCurrentLocation != null) {
            LatLng latLng = new LatLng(stop1Latitude, stop1Longitude);

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL));
        } else {
            //Toast.makeText(getContext(), "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
        }
        DetailedPathFragmentPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);
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

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                //onLocationChanged(locationResult.getLastLocation());
            }
        };
        getFusedLocationProviderClient(getContext()).requestLocationUpdates(mLocationRequest, mLocationCallback,
                Looper.myLooper());
    }


    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void createStopMarker(Stop stop) {
        ParseGeoPoint stopLocation = stop.getStopLocation();
        double stopLatitude = stopLocation.getLatitude();
        double stopLongitude = stopLocation.getLongitude();
        BitmapDescriptor smallMarkerIcon = createMarkerIcon();

        map.addMarker(new MarkerOptions()
                .position(new LatLng(stopLatitude, stopLongitude))
                .title(stop.getStopName())
                .snippet(stop.getStopDetails())
                .icon(smallMarkerIcon)
        );
        createStopCircle(stopLatitude, stopLongitude);
    }

    private void initializeViews() {
        tvPathName.setText(currentPath.getPathName());
        tvPathDescription.setText(currentPath.getPathDescription());
        rbPathRating.setRating(currentPath.getPathRating());
    }

    private void setUpMapFragment(@Nullable Bundle savedInstanceState) {
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
                    for (int i = 0; i < stops.size(); i++) {
                        createStopMarker(stops.get(i));
                    }
                }
            });
        } else {
            Toast.makeText(getContext(), "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<Stop> createStopsList() {
        stops = new ArrayList<>();
        stops.add(currentPath.getStop1());
        stops.add(currentPath.getStop2());
        stops.add(currentPath.getStop3());
        stops.add(currentPath.getStop4());
        stops.add(currentPath.getStop5());
        return stops;
    }

    private Circle createStopCircle(double stopLatitude, double stopLongitude) {
        return map.addCircle(new CircleOptions()
                .center(new LatLng(stopLatitude, stopLongitude))
                .radius(STOP_RADIUS)
                .strokeColor(Color.MAGENTA)
                .fillColor(0x55EB1465)
                .strokeWidth(4));
    }

    private BitmapDescriptor createMarkerIcon() {
        int height = MARKER_HEIGHT;
        int width = MARKER_WIDTH;
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.gem);;
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(smallMarker);
    }

    private ParseGeoPoint getLocationOfStop(Stop stop) {
        ParseGeoPoint stopLocation = null;
        try {
            stopLocation = stop.fetchIfNeeded().getParseGeoPoint("stopLocation");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return stopLocation;
    }



}
