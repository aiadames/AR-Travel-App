package com.example.artravel;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.artravel.Fragments.TabStopFragment;

public class StopFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 5;
    private String tabTitles[] = new String[] { "Stop 1", "Stop 2", "Stop 3", "Stop 4", "Stop 5"};
    private Context context;
    private final Bundle fragmentBundle;

    public StopFragmentPagerAdapter(FragmentManager fm, Context context, Bundle data) {
        super(fm);
        this.context = context;
        fragmentBundle = data;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        TabStopFragment tabStopFragment = TabStopFragment.newInstance(position + 1);
        // Set arguments to be bundle containing new path
        tabStopFragment.setArguments(this.fragmentBundle);
        return tabStopFragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
