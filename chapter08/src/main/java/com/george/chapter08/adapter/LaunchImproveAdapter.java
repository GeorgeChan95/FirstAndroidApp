package com.george.chapter08.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.george.chapter08.fragment.LaunchFregment;

public class LaunchImproveAdapter extends FragmentPagerAdapter {
    private final int[] imageArray;

    public LaunchImproveAdapter(@NonNull FragmentManager fm, int[] imageArray) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.imageArray = imageArray;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return LaunchFregment.newInstance(position, imageArray.length, imageArray[position]);
    }

    @Override
    public int getCount() {
        return imageArray.length;
    }
}
