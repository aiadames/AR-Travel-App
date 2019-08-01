package com.example.artravel.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.artravel.R;
import com.example.artravel.models.Gems;
import com.parse.ParseFile;

import org.parceler.Parcels;

public class GemDetail extends Fragment {

private Gems currentGem;
private TextView gemName;
private TextView gemDescription;
private ImageView gemImage;

    public static GemDetail newInstance() {
        return new GemDetail();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gem_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupView(view);
        Bundle bundle = this.getArguments();
        currentGem = Parcels.unwrap(bundle.getParcelable("Gems"));

        setValues(currentGem);
    }

    private void setValues(Gems currentGem) {
        gemName.setText(currentGem.getName());
        gemDescription.setText(currentGem.getDescription());

        ParseFile image = currentGem.getImage();
        if (image != null) {
            Glide.with(getContext())
                    .load(image.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(gemImage);
        }
    }

    private void setupView(View view) {

        gemName = view.findViewById(R.id.tvdetailName);
        gemDescription = view.findViewById(R.id.tvdetailDescr);
        gemImage = view.findViewById(R.id.ivProfileImag1);
    }

}
