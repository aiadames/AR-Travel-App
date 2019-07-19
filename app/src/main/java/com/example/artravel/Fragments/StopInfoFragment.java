package com.example.artravel.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.artravel.R;
import com.example.artravel.models.Path;
import com.example.artravel.models.Stop;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.ArrayList;

public class StopInfoFragment extends Fragment {

    private Stop stop;
    private Path path;
    private ArrayList<Stop> stopsList;
    private int stopIndex;

    private TextView tvInfoStopTitle;
    private ImageView ivInfoStopImage;
    private TextView tvInfoParagraph;
    private Button btnInfoNextStop;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stop_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvInfoStopTitle = view.findViewById(R.id.tvInfoStopTitle);
        ivInfoStopImage = view.findViewById(R.id.ivInfoStopImage);
        tvInfoParagraph = view.findViewById(R.id.tvInfoParagraph);
        btnInfoNextStop = view.findViewById(R.id.btnInfoNextStop);

        Bundle bundle = this.getArguments();
        stop = Parcels.unwrap(bundle.getParcelable("Stop"));
        path = Parcels.unwrap(bundle.getParcelable("Path"));
        stopsList = Parcels.unwrap(bundle.getParcelable("Stops Array"));
        stopIndex = bundle.getInt("Stop Index");


        tvInfoStopTitle.setText(stop.getStopName());
        tvInfoParagraph.setText(stop.getInfoParagraph());

        ParseFile image = stop.getStopImage();
        if (image != null) {
            Glide.with(this)
                    .load(image.getUrl())
                    .into(ivInfoStopImage);
        }

        btnInfoNextStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment stopFragment = new StopFragment();

                Bundle bundle = new Bundle();
                bundle.putParcelable("Path", Parcels.wrap(path));
                bundle.putParcelable("Stops Array", Parcels.wrap(stopsList));
                if (stopIndex < 4) {
                    stopIndex++;
                }
                bundle.putInt("Stop Index", stopIndex);
                stopFragment.setArguments(bundle);

                FragmentManager fragmentManager = ((AppCompatActivity)getActivity()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContainer, stopFragment)
                        .commit();
            }
        });


    }
}
