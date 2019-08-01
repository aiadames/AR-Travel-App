package com.example.artravel.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

    private Button btnGemLocation;
    private Button btnAnswerQuestion;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentStopInfoBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stop_info, container, false );
        View view = binding.getRoot();
        initializeBundleArguments();
        StopInfoViewModel stopInfoViewModel = new StopInfoViewModel();
        stopInfoViewModel.setStop(stop);
        stopInfoViewModel.setPath(path);
        stopInfoViewModel.setStopsList(stopsList);
        stopInfoViewModel.setStopIndex(stopIndex);
        binding.setStopInfoViewModel(stopInfoViewModel);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnGemLocation = view.findViewById(R.id.btnGemLocation);
        btnAnswerQuestion = view.findViewById(R.id.btnQuestion);

        btnGemLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });

        btnAnswerQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment questionFragment = new QuestionFragment();

                // Pass bundle with stop, path, and current stop
                Bundle bundle = new Bundle();
                bundle.putParcelable("Stop", Parcels.wrap(stop));
                bundle.putParcelable("Path", Parcels.wrap(path));
                bundle.putParcelable("Stops Array", Parcels.wrap(stopsList));
                bundle.putInt("Stop Index", stopIndex);
                questionFragment.setArguments(bundle);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContainer, questionFragment).addToBackStack("Stop Info")
                        .commit();
            }
        });

    }

    private void initializeBundleArguments() {
        // Get bundle with stops, path, and current stop
        Bundle bundle = this.getArguments();
        stop = Parcels.unwrap(bundle.getParcelable("Stop"));
        path = Parcels.unwrap(bundle.getParcelable("Path"));
        stopsList = Parcels.unwrap(bundle.getParcelable("Stops Array"));
        stopIndex = bundle.getInt("Stop Index");
    }
}
