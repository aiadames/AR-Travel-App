
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

import com.example.artravel.MainActivity;
import com.example.artravel.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.artravel.Fragments.CreateFragment;
import com.example.artravel.Fragments.HomeFragment;
import com.example.artravel.Fragments.PathsFragment;
import com.example.artravel.Fragments.PassportFragment;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Button mapButton;
    private TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        final FragmentManager fragmentManager = getSupportFragmentManager();


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = new HomeFragment();
                switch (item.getItemId()) {
                    case R.id.action_home:
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
                        break;
                }

                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
            // bottomNavigationView.setSelectedItemId(R.id.action_home);
        });
    }
}
