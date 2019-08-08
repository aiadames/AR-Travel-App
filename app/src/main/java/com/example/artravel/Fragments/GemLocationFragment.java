package com.example.artravel.Fragments;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.artravel.Activities.ARImageActivity;
import com.example.artravel.ProgressBar;
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

/* TODO -- AR may repeat if user is in the same spot, resulting in crash - need to keep track if the user has already scanned the gem */

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

@RuntimePermissions
public class GemLocationFragment extends Fragment {

    private Path path;
    private List<Stop> stopsList;
    private int stopIndex;

    private TextView tvGemLocationClue;
    private ImageView ivGemLocationImage;
    private Button btnQuestion;
    private TextView tvClueHeader;
    private TextView tvClueStop;

    private Stop stop;

    Location mCurrentLocation;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    private static final int GEM_RADIUS = 15;
    private double distanceToGem;
    private double gemLatitude;
    private double gemLongitude;

    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
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
        setUpBackground(view);

        getActivity().setTitle("Gem Location");

        // Get bundle with stops, path, and current stop
        Bundle bundle = this.getArguments();
        stop = Parcels.unwrap(bundle.getParcelable("Stop"));
        path = Parcels.unwrap(bundle.getParcelable("Path"));
        stopsList = Parcels.unwrap(bundle.getParcelable("Stops Array"));
        stopIndex = bundle.getInt("Stop Index");

        tvGemLocationClue = view.findViewById(R.id.tvGemLocationClue);
        ivGemLocationImage = view.findViewById(R.id.ivGemLocationImage);
        btnQuestion = view.findViewById(R.id.btnQuestion);
        tvClueHeader = view.findViewById(R.id.tvClueHeader);
        tvClueStop = view.findViewById(R.id.tvClueStop);
        tvClueStop.setText("Stop: " + stop.getStopName());

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


        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(5)).format(DecodeFormat.PREFER_ARGB_8888).override(320,300);
        ParseFile image = currentGem.getGemLocationImage();
        if (image != null) {
            Glide.with(this)
                    .load(image.getUrl())
                    .apply(requestOptions)
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
        Intent intent = new Intent(getActivity(), ARImageActivity.class);
        intent.putExtra("Gem", Parcels.wrap(stop.getGem()));
        intent.putExtra("Stop", Parcels.wrap(stop));
        intent.putExtra("Path", Parcels.wrap(path));
        intent.putExtra("Stops Array", Parcels.wrap(stopsList));
        intent.putExtra("Stop Index", stopIndex);
        getContext().startActivity(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        getFusedLocationProviderClient(getContext()).removeLocationUpdates(mLocationCallback);
    }




    public void setUpBackground(View view){
        ConstraintLayout constraintLayout = view.findViewById(R.id.layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
    }
}
