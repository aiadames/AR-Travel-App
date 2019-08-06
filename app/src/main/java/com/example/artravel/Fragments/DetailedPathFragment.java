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
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artravel.Activities.MapsWindowAdapter;
import com.example.artravel.Activities.PathDetailsActivity;
import com.example.artravel.DetailedPathViewModel;
import com.example.artravel.R;
import com.example.artravel.StopInfoViewModel;
import com.example.artravel.StopsAdapter;
import com.example.artravel.StopsItemTouchHelperCallback;
import com.example.artravel.databinding.FragmentDetailedPathBinding;
import com.example.artravel.databinding.FragmentStopInfoBinding;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.sql.Array;
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

    private TextView tvCompletedPath;
    private Path currentPath;

    private final static String KEY_LOCATION = "location";
    private static final int MARKER_HEIGHT = 100;
    private static final int MARKER_WIDTH = 100;
    private static final int STOP_RADIUS = 30;
    private static final float ZOOM_LEVEL = 13.0f;

    boolean inProgress = false;

    private Bundle receivedBundle;

    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentDetailedPathBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detailed_path, container, false );
        View view = binding.getRoot();
        DetailedPathViewModel detailedPathViewModel = new DetailedPathViewModel();
        // Get the bundle containing the path
        receivedBundle = this.getArguments();
        currentPath = Parcels.unwrap(receivedBundle.getParcelable("Path"));
        detailedPathViewModel.setPath(currentPath);
        binding.bottomSheet.setDetailedPathViewModel(detailedPathViewModel);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvCompletedPath = view.findViewById(R.id.tvPathCompleted);
        RecyclerView rvStops = view.findViewById(R.id.rvStops);

        getActivity().setTitle(currentPath.getPathName());

        ParseUser currentUser = ParseUser.getCurrentUser();

        if(currentPath.getCompletedPath() == true){
            tvCompletedPath.setText("You have completed this path already.");
            tvCompletedPath.setVisibility(View.VISIBLE);
            setMargins(tvCompletedPath, 0, 5, 0, 5);
        } else {
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.bottom_sheet));
            bottomSheetBehavior.setState((BottomSheetBehavior.STATE_EXPANDED));
        }

        if (receivedBundle.containsKey("Stops Array")) {
            stops = Parcels.unwrap(receivedBundle.getParcelable("Stops Array"));


            initializeStop1();

            initializeAdapter(rvStops);

            setUpMapFragment(savedInstanceState);
        } else {
            stops = createStopsList();

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
                        initializeStop1();

                        initializeAdapter(rvStops);

                        setUpMapFragment(savedInstanceState);
                    }
                });
            }
        }
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
        ParseGeoPoint stopLocation = getLocationOfStop(stop);
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
                    ArrayList<Stop> allStops = createStopsList();
                    for (int i = 0; i < allStops.size(); i++) {
                        createStopMarker(allStops.get(i));
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

    private void initializeAdapter(RecyclerView rvStops) {
        Bundle bundleToStop = new Bundle();
        bundleToStop.putParcelable("Path", Parcels.wrap(currentPath));
        bundleToStop.putParcelable("Stops Array", Parcels.wrap(stops));
        bundleToStop.putInt("Stop Index", 0);

        // Pass bundle with stops and path to the adapter
        StopsAdapter adapter = new StopsAdapter(stops, getContext(), bundleToStop);
        rvStops.setAdapter(adapter);
        rvStops.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        ItemTouchHelper.Callback callback =
                new StopsItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rvStops);
    }

    private void initializeStop1() {
        if (stops.size() >= 1) {
            stop1 = stops.get(0);
        }
        else {
            stop1 = currentPath.getStop1();
        }
        ParseGeoPoint stop1Location = getLocationOfStop(stop1);
        stop1Latitude = stop1Location.getLatitude();
        stop1Longitude = stop1Location.getLongitude();
    }

    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }
}



