package com.example.artravel;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.artravel.Activities.HomeActivity;
import com.example.artravel.Fragments.GemLocationFragment;
import com.example.artravel.Fragments.StopInfoFragment;
import com.example.artravel.models.Path;
import com.example.artravel.models.Stop;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class StopInfoViewModel {

    private Stop stop;
    private Path path;
    private ArrayList<Stop> stopsList;
    private int stopIndex;

    public void setStop(Stop stop) {
        this.stop = stop;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public void setStopsList(ArrayList<Stop> stops) {
        stopsList = stops;
    }

    public void setStopIndex(int index) {
        stopIndex = index;
    }

    public String getStopTitle() {
        return stop.getStopName();
    }

    public String getStopInfoParagraph() {
        return stop.getInfoParagraph();
    }

    public String getStopImageUrl() {
        ParseFile image = stop.getStopImage();
        if (image != null) {
            return image.getUrl();
        }
        return null;
    }

    public String getStopProfileImageUrl() {
        ParseFile image = stop.getStopProfileImage();
        if (image != null) {
            return image.getUrl();
        }
        return null;
    }

    @BindingAdapter({"bind:stopImageUrl"})
    public static void loadStopImage(ImageView view, String stopImageUrl) {
        if (stopImageUrl != null) {
            Glide.with(view.getContext())
                    .load(stopImageUrl)
                    .into(view);
        }
    }

    @BindingAdapter({"bind:stopProfileImageUrl"})
    public static void loadStopProfileImage(ImageView view, String stopProfileImageUrl) {
        if (stopProfileImageUrl != null) {
            Glide.with(view.getContext())
                    .load(stopProfileImageUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(view);
        }
    }

}

