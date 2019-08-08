package com.example.artravel.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.artravel.Activities.HomeActivity;
import com.example.artravel.MainActivity;
import com.example.artravel.MyFriendsAdapter;
import com.example.artravel.PathsAdapter;
import com.example.artravel.ProgressBar;
import com.example.artravel.R;
import com.example.artravel.StopsAdapter;
import com.example.artravel.StopsItemTouchHelperCallback;
import com.example.artravel.TopPathsAdapter;
import com.example.artravel.models.Gems;
import com.example.artravel.models.Path;
import com.example.artravel.models.Stop;
import com.google.android.material.chip.Chip;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.example.artravel.R.layout.fragment_home;

public class HomeFragment extends Fragment {

    private TextView tvWelcome;
    private TextView tvCollectedGems;
    private TextView tvContinuePath;
    private CardView cvContinuePath;
    private Path continuePath;
    private ArrayList<Stop> continuePathStops;
    private ParseUser currentUser;

    protected RecyclerView rvTopPaths;
    protected RecyclerView rvBookmarkedPaths;
    protected RecyclerView rvMyFriends;
    protected List<Path> tPaths;
    protected TopPathsAdapter tAdapter;
    protected List<Path> bPaths;
    protected TopPathsAdapter bAdapter;
    protected List<ParseUser> friends;
    protected MyFriendsAdapter friendsAdapter;
    protected LinearLayoutManager fLayoutManager;
    protected LinearLayoutManager bLayoutManager;
    protected LinearLayoutManager tLayoutManager;
    private final Random random = new Random();
    private ProgressBar progressBar;

    private Button findFriends;

    public enum reachedStop {
        STOP1(20),
        STOP2(40),
        STOP3(60),
        STOP4(80),
        STOP5(98.2);

        private final double progress;

        reachedStop(double progress) {
            this.progress = progress;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return (View) inflater.inflate(fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvCollectedGems = view.findViewById(R.id.tvCollectedGems);
        tvContinuePath = view.findViewById(R.id.tvContinuePath);
        cvContinuePath = view.findViewById(R.id.cvContinuePath);
        progressBar = view.findViewById(R.id.progressBar);
        findFriends = view.findViewById(R.id.buttonFriends);


        // query for top 10 paths based on average
        recyclerViewSetup();
        loadTopPaths();

        ParseUser currentUser = ParseUser.getCurrentUser();
        // If a user is logged in
        if (currentUser != null) {
            recyclerViewSetup2();
            loadBookmarkedPaths();
            recyclerViewSetup3();
            loadMyFriends();
            tvWelcome.setText("Welcome back " + currentUser.getUsername() + "!");
            tvCollectedGems.setVisibility(View.INVISIBLE);
            tvContinuePath.setVisibility(View.GONE);

            // Query for all of the user's collected gems
            ParseRelation<Gems> relation = currentUser.getRelation("collectedGems");
            relation.getQuery().findInBackground(new FindCallback<Gems>() {
                @Override
                public void done(List<Gems> objects, ParseException e) {
                    int numCollectedGems = objects.size();
                    if (numCollectedGems == 0) {
                        tvCollectedGems.setText("You haven't collected any gems yet. Start a tour now!");
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
                    if (objects != null && objects.size() > 0) {
                        continuePath = objects.get(0);
                        tvContinuePath.setText("Continue with the " + continuePath.getPathName() + " tour, " + currentUser.getUsername());
                    } else {
                        tvContinuePath.setText("Get started on a tour!");
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
                                        setMargins(progressBar, 0, 30, 0, 50);
                                    }

                                }
                            });
                        }
                    }
                }
            });

        }


        cvContinuePath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (continuePath != null) {
                    Fragment detailedPathFragment = new DetailedPathFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("Path", Parcels.wrap(continuePath));
                    detailedPathFragment.setArguments(bundle);

                    FragmentManager fragmentManager = ((AppCompatActivity) getActivity()).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContainer, detailedPathFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack("Home")
                            .commit();
                } else {
                    Fragment pathsFragment = new PathsFragment();

                    FragmentManager fragmentManager = ((AppCompatActivity) getActivity()).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContainer, pathsFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack("Home")
                            .commit();
                }
            }
        });



        findFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment searchFriendsFragment = new SearchFriendsFragment();
                FragmentManager fragmentManager = ((AppCompatActivity) getActivity()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContainer, searchFriendsFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack("Home")
                        .commit();
            }
        });

        double[] arr = {reachedStop.STOP1.progress, reachedStop.STOP2.progress, reachedStop.STOP3.progress,
                reachedStop.STOP4.progress, reachedStop.STOP5.progress};
        progressBar.setGoals(arr);
        progressBar.setIndicatorType(ProgressBar.IndicatorType.Circle);
        progressBar.setGoalIndicatorThickness(2);
        progressBar.setGoalIndicatorHeight(15);
    }



    protected void loadTopPaths() {
        // query to Parse backend for the top 20 paths to load into mPaths and mPathsFull, notify adapter that data has been updated
        final Path.Query pathsQuery = new Path.Query();
        pathsQuery.setLimit(4);
        pathsQuery.addDescendingOrder("pathAvgRating");
        pathsQuery.findInBackground(new FindCallback<Path>() {
            @Override
            public void done(List<Path> objects, ParseException e) {
                if (e == null) {
                    Log.d("yer", objects.get(0).getPathName());
                    Log.d("yer", objects.get(1).getPathName());
                    tPaths.addAll(objects);
                    tAdapter.notifyDataSetChanged();

                }

            }
        });
    }

    protected void loadBookmarkedPaths() {
        ParseRelation<Path> startedPaths = ParseUser.getCurrentUser().getRelation("bookmarkedPaths");
        startedPaths.getQuery().findInBackground(new FindCallback<Path>() {
            @Override
            public void done(List<Path> objects, ParseException e) {
                if (e != null){
                    e.printStackTrace();
                } else {
                    if (objects.size() == 0){

                    } else{
                        bPaths.addAll(objects);
                        bAdapter.notifyDataSetChanged();
                    }

                }
            }
        });

    }


    protected void loadMyFriends() {
        ParseRelation<ParseUser> myFriends = ParseUser.getCurrentUser().getRelation("friends");
        myFriends.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null){
                    e.printStackTrace();
                } else {
                    friends.addAll(objects);
                    friendsAdapter.notifyDataSetChanged();
                }
            }
        });

    }




    protected void recyclerViewSetup(){
        tPaths = new ArrayList<>();
        rvTopPaths = getView().findViewById(R.id.rvTopPaths);
        rvTopPaths.setHasFixedSize(true);
        tLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvTopPaths.setLayoutManager(tLayoutManager);
        tAdapter = new TopPathsAdapter(tPaths);
        rvTopPaths.setAdapter(tAdapter);
    }

    protected void recyclerViewSetup2(){
        bPaths = new ArrayList<>();
        rvBookmarkedPaths = getView().findViewById(R.id.rvBookmarkedPaths);
        rvBookmarkedPaths.setHasFixedSize(true);
        bLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvBookmarkedPaths.setLayoutManager(bLayoutManager);
        bAdapter = new TopPathsAdapter(bPaths);
        rvBookmarkedPaths.setAdapter(bAdapter);
    }

    protected void recyclerViewSetup3(){
        friends = new ArrayList<>();
        rvMyFriends = getView().findViewById(R.id.rvMyFriends);
        rvMyFriends.setHasFixedSize(true);
        fLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvMyFriends.setLayoutManager(fLayoutManager);
        friendsAdapter = new MyFriendsAdapter(friends);
        rvMyFriends.setAdapter(friendsAdapter);
    }






    private ArrayList<Stop> createStopsList() {
        ArrayList<Stop> stops = new ArrayList<>();
        stops.add(continuePath.getStop1());
        stops.add(continuePath.getStop2());
        stops.add(continuePath.getStop3());
        stops.add(continuePath.getStop4());
        stops.add(continuePath.getStop5());
        return stops;
    }

    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }



}