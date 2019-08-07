package com.example.artravel.Fragments;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.artravel.R;
import com.example.artravel.models.Gems;
import com.example.artravel.models.Path;
import com.parse.ParseFile;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.plattysoft.leonids.ParticleSystem;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class CompletedPathFragment extends Fragment {


    private TextView tvCompleted;
    private TextView tvRatePath;
    private Button btnSubmitRating;
    RatingBar rbPathRating;
    private TextView tvPathName;
    private Path path;
    private ImageView ivPathImage;

    private Button button;

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
        setUpConfetti(view);
        setUpBackground(view);

        tvCompleted = view.findViewById(R.id.tvCompleted);
        tvPathName = view.findViewById(R.id.tvPathName);
        tvPathName.setText(path.getPathName());
        ivPathImage = view.findViewById(R.id.ivPathImage);
        ParseFile pathImage = path.getPathImage();
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(4)).format(DecodeFormat.PREFER_ARGB_8888).override(200,125);

        Glide.with(getContext()).load(pathImage.getUrl()).apply(requestOptions).into(ivPathImage);

        tvRatePath = view.findViewById(R.id.tvRatePath);
        rbPathRating= (RatingBar) view.findViewById(R.id.rbPathRating);
        btnSubmitRating = view.findViewById(R.id.btnSubmitRating);
        btnSubmitRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

    public void setUpConfetti(View view){
        new ParticleSystem(getActivity(), 500, R.drawable.confetti3, 5000)
                .setSpeedRange(0.1f, 0.25f)
                .setRotationSpeedRange(90, 180)
                .setInitialRotationRange(0, 360)
                .oneShot(view, 100);


        new ParticleSystem(getActivity(), 500, R.drawable.confetti2, 5000)
                .setSpeedRange(0.1f, 0.25f)
                .setRotationSpeedRange(90, 180)
                .setInitialRotationRange(0, 360)
                .oneShot(view, 100);

        new ParticleSystem(getActivity(), 500, R.drawable.star_white, 5000)
                .setSpeedRange(0.1f, 0.25f)
                .setRotationSpeedRange(90, 180)
                .setInitialRotationRange(0, 360)
                .oneShot(view, 100);

    }


    public void setUpBackground(View view){
        ConstraintLayout constraintLayout = view.findViewById(R.id.layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
    }


}
