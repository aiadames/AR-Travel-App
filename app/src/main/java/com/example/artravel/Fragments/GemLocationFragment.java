package com.example.artravel.Fragments;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.artravel.R;
import com.example.artravel.models.Gems;
import com.example.artravel.models.Path;
import com.example.artravel.models.Stop;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

@RuntimePermissions
public class GemLocationFragment extends Fragment {

    private Path path;
    private List<Stop> stopsList;
    private int stopIndex;

    private TextView tvGemLocationClue;
    private ImageView ivGemLocationImage;
    private Button btnQuestion;
    private Stop stop;

    Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    private static final int GEM_RADIUS = 58;
    private double distanceToGem;
    private double gemLatitude;
    private double gemLongitude;

    private LocationCallback mLocationCallback;

    /*
     * Method that inflates the fragment_gem_location XML layout file for the Gem Location fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gem_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();
        stop = Parcels.unwrap(bundle.getParcelable("Stop"));
        path = Parcels.unwrap(bundle.getParcelable("Path"));
        stopsList = Parcels.unwrap(bundle.getParcelable("Stops Array"));
        stopIndex = bundle.getInt("Stop Index");

        tvGemLocationClue = view.findViewById(R.id.tvGemLocationClue);
        ivGemLocationImage = view.findViewById(R.id.ivGemLocationImage);
        btnQuestion = view.findViewById(R.id.btnQuestion);

        Gems currentGem = stop.getGem();
        try {
            tvGemLocationClue.setText(currentGem.fetchIfNeeded().getString("gemLocationClue"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseGeoPoint gemLocation = currentGem.getGemLocation();
        gemLatitude = gemLocation.getLatitude();
        gemLongitude = gemLocation.getLongitude();

        GemLocationFragmentPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);

        ParseFile image = currentGem.getGemLocationImage();
        if (image != null) {
            Glide.with(this)
                    .load(image.getUrl())
                    .into(ivGemLocationImage);
        }

        btnQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToQuestionFragment();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        GemLocationFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    public void onLocationChanged(Location location) {
        // GPS may be turned off
        if (location == null) {
            return;
        }
        // Update distance
        mCurrentLocation = location;
        Location gemLocation = new Location("");
        gemLocation.setLatitude(gemLatitude);
        gemLocation.setLongitude(gemLongitude);
        distanceToGem = mCurrentLocation.distanceTo(gemLocation);
        Toast.makeText(getContext(), "Distance: " + Math.round(distanceToGem), Toast.LENGTH_SHORT).show();

        // Switch to stop information fragment when user is within the specified radius of the stop
        if (Math.round(distanceToGem) < GEM_RADIUS) {
            switchToQuestionFragment();
        }
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
                onLocationChanged(locationResult.getLastLocation());
            }
        };
        getFusedLocationProviderClient(getContext()).requestLocationUpdates(mLocationRequest, mLocationCallback,
                Looper.myLooper());
    }

    private void switchToQuestionFragment() {
        Fragment questionFragment = new QuestionFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("Stop", Parcels.wrap(stop));
        bundle.putParcelable("Path", Parcels.wrap(path));
        bundle.putParcelable("Stops Array", Parcels.wrap(stopsList));
        bundle.putInt("Stop Index", stopIndex);
        questionFragment.setArguments(bundle);

        FragmentManager fragmentManager = ((AppCompatActivity)getActivity()).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContainer, questionFragment).addToBackStack("Stop").commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        getFusedLocationProviderClient(getContext()).removeLocationUpdates(mLocationCallback);
    }
}
