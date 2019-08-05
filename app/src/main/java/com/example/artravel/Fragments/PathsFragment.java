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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artravel.EndlessRecyclerViewScrollListener;
import com.example.artravel.PathsAdapter;
import com.example.artravel.R;
import com.example.artravel.models.Path;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class PathsFragment extends Fragment {

    protected List<Path> mPaths;
    protected List<Path> mPathsFull;
    protected RecyclerView mRecyclerViewPaths;
    protected LinearLayoutManager mLayoutManager;
    protected PathsAdapter mAdapter;
    private EndlessRecyclerViewScrollListener scrollListener;


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
        loadTopPaths();
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


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // menu item in toolbar for searching through paths by path name specifically
        inflater.inflate(R.menu.path_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
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



}


