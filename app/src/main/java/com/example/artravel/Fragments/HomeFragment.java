package com.example.artravel.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.artravel.Activities.HomeActivity;
import com.example.artravel.Activities.passportSceneform;
import com.example.artravel.MainActivity;
import com.example.artravel.R;
import com.example.artravel.models.Path;
import com.parse.ParseObject;
import com.parse.ParseUser;

import static com.example.artravel.R.layout.fragment_home;

public class HomeFragment extends Fragment {

    private Button btnTest;
    private Button launchAr;
    private TextView tvName;
    private ImageView ivProfile;
    private TextView tvEmail;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return (View) inflater.inflate(fragment_home,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnTest = view.findViewById(R.id.btnTest);

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                Intent logout = new Intent(getActivity(), MainActivity.class);
                startActivity(logout);
                Toast. makeText(getContext(), "Logout",Toast.LENGTH_SHORT).show();
            }

        });


    }
}

