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

import com.arlib.floatingsearchview.FloatingSearchView;
import com.example.artravel.EndlessRecyclerViewScrollListener;
import com.example.artravel.PathsAdapter;
import com.example.artravel.R;
import com.example.artravel.UsersAdapter;
import com.example.artravel.models.Path;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static com.example.artravel.R.layout.fragment_home;
import static com.example.artravel.R.layout.fragment_search_friends;

public class SearchFriendsFragment extends Fragment {


    protected List<ParseUser> mUsers;
    protected List<ParseUser> mUsersFull;
    protected RecyclerView mRecyclerViewUsers;
    protected LinearLayoutManager mLayoutManager;
    protected UsersAdapter mAdapter;
    public FloatingSearchView mSearchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return (View) inflater.inflate(fragment_search_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpRecyclerView();
        loadUsers();

        mSearchView = view.findViewById(R.id.floatingSearchView);
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                mAdapter.getFilter().filter(newQuery);
            }
        });
    }



    public void setUpRecyclerView() {
        mUsers = new ArrayList<>();
        mUsersFull = new ArrayList<>();
        mRecyclerViewUsers = getView().findViewById(R.id.rvUsers);
        mRecyclerViewUsers.setHasFixedSize(true);
        //create the adapter and data source
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerViewUsers.setLayoutManager(mLayoutManager);
        mAdapter = new UsersAdapter(mUsers,mUsersFull);
        mRecyclerViewUsers.setAdapter(mAdapter);


    }

    protected void loadUsers() {
        ParseUser current = ParseUser.getCurrentUser();
        // query to Parse backend for the top 20 paths to load into mPaths and mPathsFull, notify adapter that data has been updated
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.setLimit(5);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    for (ParseUser user: objects){
                        if (!(user.getObjectId().equalsIgnoreCase(current.getObjectId()))){
                            mUsers.add(user);
                            mUsersFull.add(user);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
