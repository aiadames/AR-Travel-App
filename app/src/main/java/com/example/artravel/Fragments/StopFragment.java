package com.example.artravel.Fragments;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.artravel.Activities.MapsWindowAdapter;
import com.example.artravel.Activities.StreetViewActivity;
import com.example.artravel.DetailedPathViewModel;
import com.example.artravel.MainActivity;
import com.example.artravel.R;
import com.example.artravel.StopViewModel;
import com.example.artravel.databinding.FragmentDetailedPathBinding;
import com.example.artravel.databinding.FragmentStopBinding;
import com.example.artravel.models.Path;
import com.example.artravel.models.Stop;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

import org.parceler.Parcels;

import java.util.ArrayList;


import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

@RuntimePermissions
public class StopFragment extends Fragment implements View.OnClickListener {

    private Path path;
    private ArrayList<Stop> allStops;
    private ArrayList<Stop> stopsList;
    private int stopIndex;
    private Stop currentStop;
    private double stopLatitude;
    private double stopLongitude;

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    Location mCurrentLocation;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    private static final String KEY_LOCATION = "location";
    private static final int MARKER_HEIGHT = 80;
    private static final int MARKER_WIDTH = 80;
    private static final int STOP_RADIUS = 30;
    private static final float ZOOM_LEVEL = 17.5f;

    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private TextView tvStopDistance;
    private Button btnStopInfo;
    private FloatingActionButton btnMapType;
    private FloatingActionButton btnStreetView;

    private double distanceToStop;

    private StopViewModel stopViewModel;

    /*
     * Method that inflates the fragment_stop XML layout file for the Stop fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentStopBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stop, container, false);
        View view = binding.getRoot();
        stopViewModel = new StopViewModel();
        // Get the bundle containing the path
        initializeBundleArguments();
        stopViewModel.setStop(currentStop);
        stopViewModel.setPath(path);
        binding.setStopViewModel(stopViewModel);
        binding.stopBottomSheet.setStopViewModel(stopViewModel);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        getActivity().setTitle(currentStop.getStopName());

        tvStopDistance = view.findViewById(R.id.tvStopDistance);
        btnStopInfo = view.findViewById(R.id.btnStopInfo);
        btnStreetView = view.findViewById(R.id.btnStreetView);
        btnMapType = view.findViewById(R.id.btnMapType);

        ParseGeoPoint stopLocation = getLocationOfStop(currentStop);
        stopLatitude = stopLocation.getLatitude();
        stopLongitude = stopLocation.getLongitude();
        stopViewModel.setStopLatitude(stopLatitude);
        stopViewModel.setStopLongitude(stopLongitude);

        setUpMapFragment(savedInstanceState);

        btnStopInfo.setOnClickListener(this);
        btnStreetView.setOnClickListener(this);
        btnMapType.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnStopInfo:
                switchToStopInfoFragment();
                break;

            case R.id.btnStreetView:
                Intent intent = new Intent(getActivity(), StreetViewActivity.class);
                intent.putExtra("Stop Latitude", stopLatitude);
                intent.putExtra("Stop Longitude", stopLongitude);
                startActivity(intent);
                break;
            case R.id.btnMapType:
                PopupMenu popup = new PopupMenu(getActivity(), btnMapType);
                popup.getMenuInflater()
                        .inflate(R.menu.stop_fragment_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.standardMap:
                                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                return true;
                            case R.id.satelliteMap:
                                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                return true;
                            case R.id.hybridMap:
                                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                return true;
                            default:
                                return true;
                        }
                    }
                });
                popup.show();
            default:
                break;
        }
    }

    /*
     * Method that loads the GoogleMap into the map fragment. It zooms into the stop's location
     * when it is initialized.
     */
    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            stopViewModel.setMap(map);
            StopFragmentPermissionsDispatcher.getMyLocationWithPermissionCheck(this);
            StopFragmentPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);
            LatLng latLng = new LatLng(stopLatitude, stopLongitude);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL));
        } else {
            Toast.makeText(getContext(), "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

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
                            onLocationChanged(location);
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

        // Display the connection status
        if (mCurrentLocation != null) {
            LatLng latLng = new LatLng(stopLatitude, stopLongitude);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL));
        } else {
            //Toast.makeText(getContext(), "Current location was null, enable GPS!", Toast.LENGTH_SHORT).show();
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
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    /*
     * Method that is called when the GPS location is first found and when it is found to change during the regular
     * updates. It calculates the distance of the user's location to the stop.
     */
    public void onLocationChanged(Location location) {
        // GPS may be turned off
        if (location == null) {
            return;
        }
        // Update distance
        mCurrentLocation = location;
        Location stopLocation = new Location("");
        stopLocation.setLatitude(stopLatitude);
        stopLocation.setLongitude(stopLongitude);
        distanceToStop = mCurrentLocation.distanceTo(stopLocation);
        // Switch to stop information fragment when user is within the specified radius of the stop
        if (Math.round(distanceToStop) < STOP_RADIUS) {
            switchToStopInfoFragment();
        }
        tvStopDistance.setText("Approximately " + Math.round(distanceToStop) + " m to " + currentStop.getStopName());
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }


    /*
     * Method to create a stop marker for the Stop that is passed in as an argument. It places
     * a gem marker at that position and a circle representing the area containing the gem.
     */
    private void createStopMarker(Stop stop) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gem);;
        BitmapDescriptor smallMarkerIcon = createSmallBitmapIcon(bitmap);

        ParseGeoPoint stopLocation = getLocationOfStop(stop);

        Marker mapMarker = map.addMarker(new MarkerOptions()
                .position(new LatLng(stopLocation.getLatitude(), stopLocation.getLongitude()))
                .title(stop.getStopName())
                .snippet(stop.getStopDetails())
                .icon(smallMarkerIcon)
        );

        Circle mapCircle = createStopCircle(stopLocation);

        if (stop.equals(currentStop)) {
            Bitmap stopBitmap = changeBitmapColor(bitmap, Color.CYAN);
            BitmapDescriptor stopMarkerIcon = createSmallBitmapIcon(stopBitmap);
            mapMarker.setIcon(stopMarkerIcon);
            mapCircle.setFillColor(0x302171de);
            mapCircle.setStrokeColor(Color.BLUE);
        }
    }

    private void switchToStopInfoFragment() {
        Fragment stopInfoFragment = new StopInfoFragment();

        // Create new bundle with path, stops, and current stops
        Bundle bundle = new Bundle();
        bundle.putParcelable("Stop", Parcels.wrap(currentStop));
        bundle.putParcelable("Path", Parcels.wrap(path));
        bundle.putParcelable("Stops Array", Parcels.wrap(stopsList));
        bundle.putInt("Stop Index", stopIndex);
        stopInfoFragment.setArguments(bundle);

        FragmentManager fragmentManager = ((AppCompatActivity)getActivity()).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContainer, stopInfoFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack("Stop").commit();
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

    private void initializeBundleArguments() {
        // Get bundle containing path, stops, and current stop
        Bundle bundle = this.getArguments();
        path = Parcels.unwrap(bundle.getParcelable("Path"));
        currentStop = Parcels.unwrap(bundle.getParcelable("Stop"));
        stopsList = Parcels.unwrap(bundle.getParcelable("Stops Array"));
        stopIndex = bundle.getInt("Stop Index");
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

    public static Bitmap changeBitmapColor(Bitmap sourceBitmap, int color) {
        Bitmap resultBitmap = sourceBitmap.copy(sourceBitmap.getConfig(),true);
        Paint paint = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 1);
        paint.setColorFilter(filter);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, paint);
        return resultBitmap;
    }

    private BitmapDescriptor createSmallBitmapIcon(Bitmap bitmap) {
        int height = MARKER_HEIGHT;
        int width = MARKER_WIDTH;
        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(smallMarker);
    }

    private Circle createStopCircle(ParseGeoPoint stopLocation) {
        return map.addCircle(new CircleOptions()
                .center(new LatLng(stopLocation.getLatitude(), stopLocation.getLongitude()))
                .radius(STOP_RADIUS)
                .strokeColor(Color.MAGENTA)
                .fillColor(0x55EB1465)
                .strokeWidth(4));
    }

    private ArrayList<Stop> createStopsList() {
        allStops = new ArrayList<>();
        allStops.add(path.getStop1());
        allStops.add(path.getStop2());
        allStops.add(path.getStop3());
        allStops.add(path.getStop4());
        allStops.add(path.getStop5());
        return allStops;
    }
}
