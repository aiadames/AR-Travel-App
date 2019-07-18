package com.example.artravel.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    protected ArrayList<Path> mPaths;
    protected RecyclerView mRecyclerViewPaths;
    protected RecyclerView.Adapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_paths, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setUpRecyclerView();
        mPaths.add(new Path("https://www.macworld.co.uk/cmsdata/features/3529463/facebook-logo_thumb800.jpg", "yeet", "yeerr"));
        mPaths.add(new Path("https://www.macworld.co.uk/cmsdata/features/3529463/facebook-logo_thumb800.jpg", "yEEEET", "yeRRR"));
        mAdapter.notifyDataSetChanged();
        loadTopPaths();
        Toast.makeText(getContext(), "HELLO", Toast.LENGTH_SHORT);
    }



    public void setUpRecyclerView(){
        mPaths = new ArrayList<>();
        mRecyclerViewPaths = getView().findViewById(R.id.rvPaths);
        mRecyclerViewPaths.setHasFixedSize(true);
        //create the adapter and data source
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new PathsAdapter(mPaths);
        mRecyclerViewPaths.setLayoutManager(mLayoutManager);
        mRecyclerViewPaths.setAdapter(mAdapter);
    }

    protected void loadTopPaths() {
        final Path.Query pathsQuery = new Path.Query();
        pathsQuery.getTop();
        pathsQuery.addDescendingOrder(Path.KEY_CREATED_AT);
        pathsQuery.findInBackground(new FindCallback<Path>() {
            @Override
            public void done(List<Path> objects, ParseException e) {
                if (e == null) {
                    //mPaths.addAll(objects);
                    //mAdapter.notifyDataSetChanged();

                    for (int i = 0; i < objects.size(); i++) {
                        Log.d("PathsFragment", "Post[" + i + "] = " + objects.get(i).getDescription());
                    }
                } else{
                    e.printStackTrace();
                }
            }
        });
    }
}
