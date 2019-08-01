
package com.example.artravel.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.artravel.Fragments.CompletedPathFragment;
import com.example.artravel.Fragments.DetailedPathFragment;
import com.example.artravel.MainActivity;
import com.example.artravel.R;
import com.example.artravel.StopFragmentPagerAdapter;
import com.example.artravel.models.Path;
import com.example.artravel.models.Stop;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.artravel.Fragments.CreateFragment;
import com.example.artravel.Fragments.HomeFragment;
import com.example.artravel.Fragments.PathsFragment;
import com.example.artravel.Fragments.PassportFragment;
import com.google.android.material.tabs.TabLayout;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment initialize;
        initialize = new HomeFragment();
        fragmentManager.beginTransaction().replace(R.id.flContainer, initialize).commit();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("Fragment")) {
            Fragment detailedPathFragment = new DetailedPathFragment();
            Fragment doneFragment = new CompletedPathFragment();

            Path path = Parcels.unwrap(intent.getParcelableExtra("Path"));
            ArrayList<Stop> stopsList = Parcels.unwrap(intent.getParcelableExtra("Stops Array"));
            int stopIndex = intent.getIntExtra("Stop Index", 1);
            Stop stop = Parcels.unwrap(intent.getParcelableExtra("Stop"));


            // Pass bundle with path
            Bundle bundle = new Bundle();
            bundle.putParcelable("Path", Parcels.wrap(path));
            if (stopIndex < stopsList.size() - 1) {
                //stopIndex++;
                for (int i = 0; i < stopsList.size(); i++) {
                    if (stop.getObjectId().equals(stopsList.get(i).getObjectId())) {
                        stopsList.remove(stopsList.get(i));
                    }
                }
                bundle.putParcelable("Stops Array", Parcels.wrap(stopsList));
                // add gems to relation of specific user for passport use
                detailedPathFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.flContainer, detailedPathFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack("Stop")
                        .commit();
            } else if (stopIndex == stopsList.size() - 1){
                // query for started paths relation and remove this path
                // query for completed paths relation and add this path
                ParseUser user = ParseUser.getCurrentUser();
                ParseRelation<Path> startedPaths = user.getRelation("startedPaths");
                startedPaths.remove(path);
                user.saveInBackground();
                ParseRelation<Path> completedPaths = user.getRelation("completedPaths");
                completedPaths.add(path);
                user.saveInBackground();
                doneFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.flContainer, doneFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack("Stop")
                        .commit();
            }
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = new HomeFragment();
                switch (item.getItemId()) {
                    case R.id.Home:
                        fragment = new HomeFragment();
                        //Toast. makeText(HomeActivity.this, "Compose",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_create:
                        fragment = new CreateFragment();
                        //Toast. makeText(HomeActivity.this, "profile",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_paths:
                        fragment = new PathsFragment();
                        //Toast. makeText(HomeActivity.this, "paths",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_passport:
                        fragment = new PassportFragment();
                        //Toast. makeText(HomeActivity.this, "home",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        // shouldn't we be making a new HomeFragment here as the default?
                        break;
                }

                // will move this later above as home fragment not complete and will not launch other fragments, cannot add to back stack? or is it needed?
                // set HomeActivity as default first backstack item for time being
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack("home").commit();

                return true;
            }
            // bottomNavigationView.setSelectedItemId(R.id.action_home);
        });
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

}
