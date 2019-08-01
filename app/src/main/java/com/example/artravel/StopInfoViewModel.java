package com.example.artravel;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.artravel.Activities.HomeActivity;
import com.example.artravel.Fragments.GemLocationFragment;
import com.example.artravel.Fragments.StopInfoFragment;
import com.example.artravel.models.Path;
import com.example.artravel.models.Stop;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.ArrayList;

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

    public String getImageUrl() {
        ParseFile image = stop.getStopImage();
        if (image != null) {
            return image.getUrl();
        }
        return null;
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        if (imageUrl != null) {
            Glide.with(view.getContext())
                    .load(imageUrl)
                    .into(view);
        }
    }

}

