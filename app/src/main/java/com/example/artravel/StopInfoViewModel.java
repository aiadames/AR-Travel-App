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

    // Private instance variable for the current stop
    private Stop stop;

    // Public method to set the current stop
    public void setStop(Stop stop) {
        this.stop = stop;
    }

    // Method that returns and binds the name of the stop
    public String getStopTitle() {
        return stop.getStopName();
    }

    // Method that returns and binds the stop's info paragraph
    public String getStopInfoParagraph() {
        return stop.getInfoParagraph();
    }

    // Method that returns the String URL of the stop image
    public String getStopImageUrl() {
        ParseFile image = stop.getStopImage();
        if (image != null) {
            return image.getUrl();
        }
        return null;
    }

    // Method that returns the String URL of the stop's profile image
    public String getStopProfileImageUrl() {
        ParseFile image = stop.getStopProfileImage();
        if (image != null) {
            return image.getUrl();
        }
        return null;
    }

    // Method that loads the stop image into the image view
    @BindingAdapter({"bind:stopImageUrl"})
    public static void loadStopImage(ImageView view, String stopImageUrl) {
        if (stopImageUrl != null) {
            Glide.with(view.getContext())
                    .load(stopImageUrl)
                    .into(view);
        }
    }

    // Method that loads the stop profile image into the image view
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

