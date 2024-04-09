package com.george.chapter11.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.george.chapter11.fragment.BookCoverFragment;
import com.george.chapter11.fragment.BookDetailFragment;

public class GoodsPagerAdapter extends FragmentPagerAdapter {
    private String[] mTitleArray; // 声明一个标题文字数组

    public GoodsPagerAdapter(FragmentManager fm, String[] titleArray) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.mTitleArray = titleArray;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new BookCoverFragment();
            case 1:
                return new BookDetailFragment();
            default:
                break;
        }
        return new BookCoverFragment();
    }

    @Override
    public int getCount() {
        return mTitleArray.length;
    }

    /**
     * 返回ViewPager位置对应的标题
     * 在这里由于使用toolbar作为标签栏，所以这个没有用
     * @param position
     * @return
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleArray[position];
    }
}
