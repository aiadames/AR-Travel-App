package com.example.artravel.Fragments;

import android.animation.Animator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.artravel.R;
import com.example.artravel.StopInfoViewModel;
import com.example.artravel.databinding.FragmentStopInfoBinding;
import com.example.artravel.models.Gems;
import com.example.artravel.models.Path;
import com.example.artravel.models.Stop;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseFile;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;

public class StopInfoFragment extends Fragment {

    private Stop stop;
    private Path path;
    private ArrayList<Stop> stopsList;
    private int stopIndex;

    private FloatingActionButton fabGemlocation;

    /*
     * Method that creates the view for the StopInfoFragment. It handles the bundle that is
     * passed in and sets up the view model for the fragment data binding.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentStopInfoBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stop_info, container, false );
        View view = binding.getRoot();
        initializeBundleArguments();
        StopInfoViewModel stopInfoViewModel = setUpViewModel();
        binding.setStopInfoViewModel(stopInfoViewModel);
        return view;
    }

    /*
     * Method that is called when the view for the fragment has been created. It handles
     * part of the data binding and the logic for the buttons.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fabGemlocation = view.findViewById(R.id.fabGemLocation);

        // On click method for gem location button
        fabGemlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToGemLocationFragment();
            }
        });
    }

    /*
     * Method that gets the bundle with stops, path, and the current
     * stop from the previous fragment.
     */
    private void initializeBundleArguments() {
        Bundle bundle = this.getArguments();
        stop = Parcels.unwrap(bundle.getParcelable("Stop"));
        path = Parcels.unwrap(bundle.getParcelable("Path"));
        stopsList = Parcels.unwrap(bundle.getParcelable("Stops Array"));
        stopIndex = bundle.getInt("Stop Index");
    }

    /*
     * Method that passes the data about the stops and paths to set up the
     * view model that binds the views to the data associated with the
     * paths and stops.
     */
    private StopInfoViewModel setUpViewModel() {
        StopInfoViewModel stopInfoViewModel = new StopInfoViewModel();
        stopInfoViewModel.setStop(stop);
        stopInfoViewModel.setPath(path);
        stopInfoViewModel.setStopsList(stopsList);
        stopInfoViewModel.setStopIndex(stopIndex);
        return stopInfoViewModel;
    }

    /*
     * Method that sets up the bundle to pass data between fragments
     * and switches to the Gem Location clue fragment.
     */
    private void switchToGemLocationFragment() {
        Fragment gemLocationFragment = new GemLocationFragment();

        // Pass bundle with stops, path, and current stop
        Bundle bundle = new Bundle();
        bundle.putParcelable("Stop", Parcels.wrap(stop));
        bundle.putParcelable("Path", Parcels.wrap(path));
        bundle.putParcelable("Stops Array", Parcels.wrap(stopsList));
        bundle.putInt("Stop Index", stopIndex);
        gemLocationFragment.setArguments(bundle);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContainer, gemLocationFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack("Stop Info").commit();
    }

}
