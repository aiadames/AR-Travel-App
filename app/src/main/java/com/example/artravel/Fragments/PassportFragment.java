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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artravel.Activities.ARGemViewer;
import com.example.artravel.GemsAdapter;
import com.example.artravel.R;
import com.example.artravel.models.Gems;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static com.example.artravel.R.layout.fragment_passport;

public class PassportFragment extends Fragment{
    private RecyclerView rvGems;
    private GemsAdapter adapter;
    private List<Gems> mGems;

    private ImageView profile;
    private TextView username;
    private TextView gemCount;

    private FloatingActionButton fab;
    private static final String TAG = "PassportFragment";

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

        getActivity().setTitle("Passport");
        setupView(view);
        queryGems();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Ar = new Intent(getActivity(), ARGemViewer.class);
                startActivity(Ar);
                //Toast. makeText(getContext(), "Ar frag launch",Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void queryGems() {

        ParseQuery<Gems> postQuery = new ParseQuery<Gems>(Gems.class);
       // postQuery.include(Gems.KEY_USER);
        ParseUser user = ParseUser.getCurrentUser();
        if (user == null){
            Toast.makeText(getContext(), "user null", Toast.LENGTH_SHORT).show();
        }
        else
        {
           // Toast.makeText(getContext(), "user " + user.getUsername()+ " is not null", Toast.LENGTH_SHORT).show();
        }

        ParseRelation<Gems> relation;
        relation = user.getRelation("collectedGems");
        relation.getQuery().findInBackground(new FindCallback<Gems>() {
            @Override
            public void done(List<Gems> userGems, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error in query");
                    e.printStackTrace();
                    return;
                }
                mGems.addAll(userGems);
                gemCount.setText(("You've collected " + mGems.size() + " gems this week"));

               // Toast.makeText(getContext(), numCollected + " gems collected", Toast.LENGTH_SHORT).show();
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

        username = view.findViewById(R.id.tvUsername);
        gemCount = view.findViewById(R.id.tvPrompt);


        fab = view.findViewById(R.id.floatingActionButton2);

    }

}
