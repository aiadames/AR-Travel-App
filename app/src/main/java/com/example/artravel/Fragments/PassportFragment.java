package com.example.artravel.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artravel.GemsAdapter;
import com.example.artravel.R;
import com.example.artravel.models.Gems;
import com.example.artravel.models.Path;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static com.example.artravel.R.layout.fragment_home;
import static com.example.artravel.R.layout.fragment_passport;

public class PassportFragment extends Fragment {
    private RecyclerView rvGems;
    private GemsAdapter adapter;
    private List<Gems> mGems;
    private static final String TAG = "PassportFragment";

  //  private Button btnTest;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return (View) inflater.inflate(fragment_passport,container, false);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupView(view);
        queryGems();

    }


    private void queryGems() {

        ParseQuery<Gems> postQuery = new ParseQuery<Gems>(Gems.class);
       // postQuery.include(Gems.KEY_USER);
        postQuery.orderByDescending("createdAt");
        postQuery.findInBackground(new FindCallback<Gems>() {
            @Override
            public void done(List<Gems> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error in query");
                    e.printStackTrace();
                    return;
                }


                mGems.addAll(posts);
                adapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.passport_menu, menu);
    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                Fragment profile = new ProfileFragment();

                FragmentManager fragmentManager = ((AppCompatActivity)getActivity()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContainer, profile).addToBackStack("Passport")
                        .commit();

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void setupView(View view){

        mGems= new ArrayList<>();
        rvGems= view.findViewById(R.id.rvRecyclerView);
        adapter = new GemsAdapter( mGems,getContext());
        rvGems.setAdapter(adapter);

        rvGems.setLayoutManager(new GridLayoutManager(getContext(),3));


    }





}
