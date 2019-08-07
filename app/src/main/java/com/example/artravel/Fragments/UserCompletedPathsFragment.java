package com.example.artravel.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artravel.CompletedPathsAdapter;
import com.example.artravel.EndlessRecyclerViewScrollListener;
import com.example.artravel.PathsAdapter;
import com.example.artravel.R;
import com.example.artravel.models.Path;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static com.example.artravel.R.layout.fragment_profile;
import static com.example.artravel.R.layout.fragment_user_completed_paths;

public class UserCompletedPathsFragment extends Fragment {

    protected List<Path> mPaths;
    protected RecyclerView mRecyclerViewPaths;
    protected LinearLayoutManager mLayoutManager;
    protected CompletedPathsAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  (View) inflater.inflate(fragment_user_completed_paths, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Completed Path");
        loadCompletedPaths();
        setUpRecyclerView();

    }

    public void setUpRecyclerView() {
        mPaths = new ArrayList<>();
        mRecyclerViewPaths = getView().findViewById(R.id.rvPaths);
        mRecyclerViewPaths.setHasFixedSize(true);
        //create the adapter and data source
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerViewPaths.setLayoutManager(mLayoutManager);
        mAdapter = new CompletedPathsAdapter(mPaths);
        mRecyclerViewPaths.setAdapter(mAdapter);
    }


    // query for a relation of completed paths for a specific user:
    // add all returned objects from query to mPaths and notify adapter to update to display values
    public void loadCompletedPaths(){
        ParseRelation<Path> completedPaths = ParseUser.getCurrentUser().getRelation("completedPaths");
        completedPaths.getQuery().findInBackground(new FindCallback<Path>() {
            @Override
            public void done(List<Path> objects, ParseException e) {
                if (e != null){
                    e.printStackTrace();
                } else {
                    mPaths.addAll(objects);
                    mAdapter.notifyDataSetChanged();


                    // query for a relation of started paths for a specific user:
                    // double for loop to iterate through all paths in existence and returned paths a user has started
                    // if exist in both: switch path's started attribute to true
                    ParseRelation<Path> startedPaths = ParseUser.getCurrentUser().getRelation("startedPaths");
                    startedPaths.getQuery().findInBackground(new FindCallback<Path>() {
                        @Override
                        public void done(List<Path> objects1, ParseException e) {
                            if (e != null){
                                e.printStackTrace();
                            } else {
                                for (int x = 0; x < mPaths.size(); x++) {
                                    for (int i = 0; i < objects1.size(); i++) {
                                        if (objects1.get(i).getObjectId().equals(mPaths.get(x).getObjectId())) {
                                            mPaths.get(x).setStartedPath();
                                        }
                                    }
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    });

                    // query for a relation of completed paths for a specific user:
                    // double for loop to iterate through all paths in existence and returned paths a user has completed
                    // if exist in both: switch path's completed attribute to true
                    ParseRelation<Path> completedPaths = ParseUser.getCurrentUser().getRelation("completedPaths");
                    completedPaths.getQuery().findInBackground(new FindCallback<Path>() {
                        @Override
                        public void done(List<Path> objects2, ParseException e) {
                            if (e != null){
                                e.printStackTrace();
                            } else { ;
                                for (int x = 0; x < mPaths.size(); x++) {
                                    for (int i = 0; i < objects2.size(); i++) {
                                        if (objects2.get(i).getObjectId().equals(mPaths.get(x).getObjectId())) {
                                            mPaths.get(x).setCompletedPath();

                                        }
                                    }
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    });

                    // query for a relation of completed paths for a specific user:
                    // double for loop to iterate through all paths in existence and returned paths a user has completed
                    // if exist in both: switch path's completed attribute to true
                    ParseRelation<Path> bookmarkedPaths = ParseUser.getCurrentUser().getRelation("bookmarkedPaths");
                    bookmarkedPaths.getQuery().findInBackground(new FindCallback<Path>() {
                        @Override
                        public void done(List<Path> objects3, ParseException e) {
                            if (e != null){
                                e.printStackTrace();
                            } else { ;
                                for (int x = 0; x < mPaths.size(); x++) {
                                    for (int i = 0; i < objects3.size(); i++) {
                                        if (objects3.get(i).getObjectId().equals(mPaths.get(x).getObjectId())) {
                                            mPaths.get(x).setPathBookmarked();
                                        }
                                    }
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    });



















                }
            }
        });
    }












}
