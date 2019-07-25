package com.example.artravel.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.artravel.R;
import com.example.artravel.models.Gems;
import com.example.artravel.models.Path;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class CompletedPathFragment extends Fragment {


    private TextView tvCompleted;
    private TextView tvRatePath;
    private Button btnSubmitRating;
    RatingBar rbPathRating;
    TextView tvUserRating;
    private Path path;


    ArrayList<Float> yer;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_completed_path, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeValues();

        tvCompleted = view.findViewById(R.id.tvCompleted);
        tvRatePath = view.findViewById(R.id.tvRatePath);
        rbPathRating= (RatingBar) view.findViewById(R.id.rbPathRating);
        tvUserRating = (TextView) view.findViewById(R.id.tvUserRating);
        btnSubmitRating = view.findViewById(R.id.btnSubmitRating);
        btnSubmitRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvUserRating.setText("Your rating is: " + rbPathRating.getRating());
                Float Rating = (rbPathRating.getRating());
                path.setPathRating(Rating);
                path.saveInBackground();

                ParseUser user = ParseUser.getCurrentUser();
                ParseRelation<Gems> relation = user.getRelation("collectedGems");
                relation.add(path.getPathGem());
                user.saveInBackground();

                Fragment backToPassport= new PassportFragment();
                FragmentManager fragmentManager = ((AppCompatActivity)getActivity()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContainer, backToPassport).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack("completed")
                        .commit();
            }
        });
    }



    private void initializeValues() {
        Bundle bundle = this.getArguments();
        path = Parcels.unwrap(bundle.getParcelable("Path"));

    }



}
