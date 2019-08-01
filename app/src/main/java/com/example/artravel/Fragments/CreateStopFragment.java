package com.example.artravel.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.artravel.Activities.HomeActivity;
import com.example.artravel.R;
import com.example.artravel.StopFragmentPagerAdapter;
import com.example.artravel.models.Path;
import com.google.android.material.tabs.TabLayout;

import org.parceler.Parcels;

public class CreateStopFragment extends Fragment {

    private String pathName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_stop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get bundle from CreateFragment containing the newly made path
        Bundle bundle = this.getArguments();

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        // Set adapter and pass in bundle with new path
        viewPager.setAdapter(new StopFragmentPagerAdapter(getActivity().getSupportFragmentManager(),
                getContext(), bundle));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
}
