package com.example.artravel.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.artravel.R;
import com.example.artravel.models.Stop;
import com.parse.ParseFile;

import org.parceler.Parcels;

public class StopDetailsFragment extends Fragment {

    private Stop stop;

    private TextView tvStopDetails;
    private ImageView ivStopDetails;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stop_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Bundle bundle = this.getArguments();
        stop = Parcels.unwrap(bundle.getParcelable("Stop"));

        tvStopDetails = view.findViewById(R.id.tvStopDetails);
        ivStopDetails = view.findViewById(R.id.ivStopDetails);

        tvStopDetails.setText(stop.getStopDetails());

        ParseFile image = stop.getStopDetailsImage();
        if (image != null) {
            Glide.with(this)
                    .load(image.getUrl())
                    .into(ivStopDetails);
        }
    }
}
