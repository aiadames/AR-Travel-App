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

import com.example.artravel.PathsAdapter;
import com.example.artravel.R;
import com.example.artravel.models.Path;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class PathsFragment extends Fragment {

    protected List<Path> mPaths;
    protected List<Path> mPathsFull;
    protected RecyclerView mRecyclerViewPaths;
    //protected RecyclerView.Adapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected PathsAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_paths, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadTopPaths();
        setUpRecyclerView();


    }



    public void setUpRecyclerView(){
        mRecyclerViewPaths = getView().findViewById(R.id.rvPaths);
        mRecyclerViewPaths.setHasFixedSize(true);

        //create the adapter and data source
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new PathsAdapter(mPaths, mPathsFull);

        mRecyclerViewPaths.setLayoutManager(mLayoutManager);
        mRecyclerViewPaths.setAdapter(mAdapter);
    }

    protected void loadTopPaths() {
        mPaths = new ArrayList<>();
        mPathsFull = new ArrayList<>();
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
                            Log.d("PathsFragment", "Post[" + i + "] = " + objects.get(i).getDescription());
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
}
