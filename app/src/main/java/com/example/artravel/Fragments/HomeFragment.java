package com.example.artravel.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.artravel.Activities.HomeActivity;
import com.example.artravel.Activities.passportSceneform;
import com.example.artravel.MainActivity;
import com.example.artravel.R;
import com.example.artravel.models.Path;
import com.parse.ParseObject;
import com.parse.ParseUser;

import static com.example.artravel.R.layout.fragment_home;

public class HomeFragment extends Fragment {

    private Button btnTest;
    private Button launchAr;
    private TextView tvName;
    private ImageView ivProfile;
    private TextView tvEmail;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return (View) inflater.inflate(fragment_home,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnTest = view.findViewById(R.id.btnTest);

        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvCollectedGems = view.findViewById(R.id.tvCollectedGems);
        tvContinuePath = view.findViewById(R.id.tvContinuePath);
        cvContinuePath = view.findViewById(R.id.cvContinuePath);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);


        ParseUser currentUser =  ParseUser.getCurrentUser();
        // If a user is logged in
        if (currentUser != null) {
            tvWelcome.setText("Welcome back " + currentUser.getUsername() + "!");
            tvCollectedGems.setVisibility(View.INVISIBLE);
            tvContinuePath.setVisibility(View.INVISIBLE);

            // Query for all of the user's collected gems
            ParseRelation<Gems> relation = currentUser.getRelation("collectedGems");
            relation.getQuery().findInBackground(new FindCallback<Gems>() {
                @Override
                public void done(List<Gems> objects, ParseException e) {
                    int numCollectedGems = objects.size();
                    if (numCollectedGems == 0) {
                        tvCollectedGems.setText("You haven't collected any gems yet. Start a path now!");
                    } else if (numCollectedGems == 1) {
                        tvCollectedGems.setText("You have collected " + numCollectedGems + " gem so far!");
                    } else {
                        tvCollectedGems.setText("You have collected " + numCollectedGems + " gems so far! Keep it up!");
                    }
                    tvCollectedGems.setVisibility(View.VISIBLE);
                }
            });


            continuePath = null;
            ParseRelation<Path> pathRelation = currentUser.getRelation("startedPaths");
            pathRelation.getQuery().findInBackground(new FindCallback<Path>() {
                @Override
                public void done(List<Path> objects, ParseException e) {
                    if (objects.size() > 0) {
                        continuePath = objects.get(0);
                        tvContinuePath.setText("Continue with " + continuePath.getPathName() + ", " + currentUser.getUsername());
                    }
                    else {
                        tvContinuePath.setText("Get started on a path!");
                    }
                    tvContinuePath.setVisibility(View.VISIBLE);

                    if (continuePath != null) {
                        continuePathStops = createStopsList();
                        if (currentUser != null) {
                            ParseRelation<Stop> relation = currentUser.getRelation("visitedStops");
                            relation.getQuery().findInBackground(new FindCallback<Stop>() {
                                @Override
                                public void done(List<Stop> objects, ParseException e) {
                                    if (e != null) {
                                        e.printStackTrace();
                                    } else {
                                        int stopCount = 0;
                                        for (int i = 0; i < continuePathStops.size(); i++) {
                                            for (int j = 0; j < objects.size(); j++) {
                                                if (continuePathStops.get(i).getObjectId().equals(objects.get(j).getObjectId())) {
                                                    stopCount++;
                                                }
                                            }
                                        }
                                        progressBar.setProgress(stopCount * 20);
                                        progressBar.setVisibility(View.VISIBLE);
                                    }

                                }
                            });
                        }
                    }
                }
            });

        }
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                Intent logout = new Intent(getActivity(), MainActivity.class);
                startActivity(logout);
                Toast. makeText(getContext(), "Logout",Toast.LENGTH_SHORT).show();
            }

        });


    }
}

