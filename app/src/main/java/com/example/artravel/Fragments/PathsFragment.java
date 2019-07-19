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
                loadEndless();
            }
        };
        mRecyclerViewPaths.addOnScrollListener(scrollListener);

    }

    protected void loadTopPaths() {

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


    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
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


