package com.example.artravel.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.example.artravel.EndlessRecyclerViewScrollListener;
import com.example.artravel.PathsAdapter;
import com.example.artravel.R;
import com.example.artravel.models.Path;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PathsFragment extends Fragment {

    protected List<Path> mPaths;
    protected List<Path> mPathsFull;
    protected RecyclerView mRecyclerViewPaths;
    protected LinearLayoutManager mLayoutManager;
    protected PathsAdapter mAdapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private List<String> selectedChips;
    public ChipGroup chipFilters;
    public FloatingSearchView mSearchView;

    List<Path> myFilteredPaths;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_paths, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Tours");
        setUpRecyclerView();
        chipFilters = (ChipGroup) view.findViewById(R.id.chipFilters);
        loadTopPaths();
        checkFilters(chipFilters);
        mSearchView = view.findViewById(R.id.floatingSearchView);

        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                mAdapter.getFilter().filter(newQuery);
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }


    public void setUpRecyclerView() {
        mPaths = new ArrayList<>();
        mPathsFull = new ArrayList<>();
        mRecyclerViewPaths = getView().findViewById(R.id.rvPaths);
        mRecyclerViewPaths.setHasFixedSize(true);
        //create the adapter and data source
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerViewPaths.setLayoutManager(mLayoutManager);
        mAdapter = new PathsAdapter(mPaths, mPathsFull);
        mRecyclerViewPaths.setAdapter(mAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if (mAdapter.getFilter() == null) {
                    loadEndless();
                } else{
                    Log.d("Filter On", "Filter is on, do not query for more paths");
                }
            }
        };
        mRecyclerViewPaths.addOnScrollListener(scrollListener);

    }

    protected void loadTopPaths() {
        // query to Parse backend for the top 20 paths to load into mPaths and mPathsFull, notify adapter that data has been updated
        final Path.Query pathsQuery = new Path.Query();
        pathsQuery.getTop();
        pathsQuery.addDescendingOrder(Path.KEY_CREATED_AT);
        pathsQuery.findInBackground(new FindCallback<Path>() {
            @Override
            public void done(List<Path> objects, ParseException e) {
                if (e == null) {
                    mPaths.addAll(objects);
                    mPathsFull.addAll(objects);
                    mAdapter.notifyDataSetChanged();

                    // query for a relation of started paths for a specific user:
                    // double for loop to iterate through all paths in existence and returned paths a user has started
                    // if exist in both: switch path's started attribute to true
                    ParseRelation<Path> startedPaths = ParseUser.getCurrentUser().getRelation("startedPaths");
                    startedPaths.getQuery().findInBackground(new FindCallback<Path>() {
                        @Override
                        public void done(List<Path> objects, ParseException e) {
                            if (e != null){
                                e.printStackTrace();
                            } else {
                                for (int x = 0; x < mPathsFull.size(); x++) {
                                    for (int i = 0; i < objects.size(); i++) {
                                        if (objects.get(i).getObjectId().equals(mPathsFull.get(x).getObjectId())) {
                                            mPathsFull.get(x).setStartedPath();
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
                        public void done(List<Path> objects, ParseException e) {
                            if (e != null){
                                e.printStackTrace();
                            } else { ;
                                for (int x = 0; x < mPathsFull.size(); x++) {
                                    for (int i = 0; i < objects.size(); i++) {
                                        if (objects.get(i).getObjectId().equals(mPathsFull.get(x).getObjectId())) {
                                            mPathsFull.get(x).setCompletedPath();

                                        }
                                    }
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                    for (int i = 0; i < objects.size(); i++) {
                        Log.d("PathsFragment", "Post[" + i + "] = " + objects.get(i).getPathDescription());
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    // append the next page of data into the adapter via endless scroll
    // sends a network request to Parse and appends new data items to your adapter based on last Path object in mPaths
    public void loadEndless() {
        final Path.Query pathsQuery = new Path.Query();
        pathsQuery.getTop();
        pathsQuery.whereLessThan("createdAt", mPaths.get(mPaths.size() - 1).getCreatedAt());
        pathsQuery.addDescendingOrder(Path.KEY_CREATED_AT);
        pathsQuery.findInBackground(new FindCallback<Path>() {
            @Override
            public void done(List<Path> objects, ParseException e) {
                if (e == null) {
                    mPaths.addAll(objects);
                    mPathsFull.addAll(objects);
                    mAdapter.notifyDataSetChanged();
                    scrollListener.resetState();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }


    // REFACTORRRR
    public void filterChips(){
        Log.d("chip", "filtering");
        myFilteredPaths = new ArrayList<>();
        mPaths.clear();
        mAdapter.notifyDataSetChanged();
        final Path.Query pathsQuery = new Path.Query();
        pathsQuery.findInBackground(new FindCallback<Path>() {
            @Override
            public void done(List<Path> objects, ParseException e) {


                ParseRelation<Path> startedPaths = ParseUser.getCurrentUser().getRelation("startedPaths");
                startedPaths.getQuery().findInBackground(new FindCallback<Path>() {
                    @Override
                    public void done(List<Path> objects3, ParseException e) {
                        if (e != null){
                            e.printStackTrace();
                        } else {
                            for (int x = 0; x < objects.size(); x++) {
                                for (int i = 0; i < objects3.size(); i++) {
                                    if (objects3.get(i).getObjectId().equals(objects.get(x).getObjectId())) {
                                        objects.get(x).setStartedPath();
                                    }
                                }
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                });


                ParseRelation<Path> completedPaths = ParseUser.getCurrentUser().getRelation("completedPaths");
                completedPaths.getQuery().findInBackground(new FindCallback<Path>() {
                    @Override
                    public void done(List<Path> objects3, ParseException e) {
                        if (e != null){
                            e.printStackTrace();
                        } else {
                            for (int x = 0; x < objects.size(); x++) {
                                for (int i = 0; i < objects3.size(); i++) {
                                    if (objects3.get(i).getObjectId().equals(objects.get(x).getObjectId())) {
                                        objects.get(x).setCompletedPath();
                                    }
                                }
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                });


                for (Path path: objects){
                    for(int i = 0; i<path.getPathTheme().size();i++){
                     //   Log.d("chip", path.getPathName()+ ": "+ path.getPathTheme().get(i));
                     //   Log.d("chip", selectedChips.get(0));
                        if (selectedChips.contains(path.getPathTheme().get(i))){
                            Log.d("chip", "match!");
                            mPaths.add(path);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });

    }


    public void checkFilters(ChipGroup filters){
        selectedChips = new ArrayList<>();
        for (int i = 0; i < filters.getChildCount(); i++) {
            Chip chip = (Chip)filters.getChildAt(i);
            // Set the chip checked change listener
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(chip.isChecked()){
                        Log.d("chip", "clicked!");
                        chip.setChipBackgroundColorResource(R.color.grey);
                        selectedChips.add(chip.getText().toString());
                        Log.d("chip", "size of list (1) : "+ selectedChips.size());
                    } else if (! chip.isChecked()){
                        Log.d("chip", "unclicked!");
                        chip.setChipBackgroundColorResource(R.color.colorOnSurface);
                        if (selectedChips.contains(chip.getText().toString())){
                            Log.d("chip", "is in list");
                            selectedChips.remove(chip.getText().toString());
                            Log.d("chip", "size of list : "+ selectedChips.size());
                            if (selectedChips.size()== 0){
                                Log.d("chip", "no filters!");
                                mPaths.clear();
                                mPathsFull.clear();
                                mAdapter.notifyDataSetChanged();
                                loadTopPaths();
                            }

                        }
                    }
                    filterChips();
                }
            });
        }
    }
}


