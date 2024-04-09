package com.george.chapter11.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.george.chapter11.fragment.TabFirstFragment;
import com.george.chapter11.fragment.TabSecondFragment;
import com.george.chapter11.fragment.TabThirdFragment;

public class TabPagerAdapter extends FragmentPagerAdapter {

    /**
     * 碎片页适配器的构造方法，传入碎片管理器
     * @param fm
     */
    public TabPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TabFirstFragment();
            case 1:
                return new TabSecondFragment();
            case 2:
                return new TabThirdFragment();
            default:
                break;
        }
        return new TabFirstFragment();
    }

    @Override
    public int getCount() {
        return 3;
    }
}
