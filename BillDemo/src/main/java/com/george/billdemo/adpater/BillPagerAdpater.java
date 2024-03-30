package com.george.billdemo.adpater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.george.billdemo.fragment.BillFragment;

public class BillPagerAdpater extends FragmentPagerAdapter {

    private int mYear;

    public BillPagerAdpater(FragmentManager fm, int year) {
        // 会将当前fragment设置为Resume的状态，把上个fragment设置成Start的状态。
        // 从而可以通过fragment的onResume()来懒加载数据。
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.mYear = year;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        String month = String.format("%02d", (position+1));
        BillFragment fragment = BillFragment.newInstance(mYear + month);
        return fragment;
    }

    /**
     * 返回tap的数量，一年有12月
     * @return
     */
    @Override
    public int getCount() {
        return 12;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return (position + 1) + "月份";
    }
}
