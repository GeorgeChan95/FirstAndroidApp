package com.george.chapter11.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.george.chapter11.entity.GoodsInfo;
import com.george.chapter11.fragment.MobileFragment;

import java.util.ArrayList;
import java.util.List;

public class MobilePagerAdapter extends FragmentStateAdapter {

    // 声明一个商品列表
    private List<GoodsInfo> mGoodsList = new ArrayList<GoodsInfo>();
    // 声明一个上下文对象
    private final Context mContext;

    public MobilePagerAdapter(FragmentActivity fragmentActivity, List<GoodsInfo> goodsList, Context context) {
        super(fragmentActivity);
        this.mGoodsList = goodsList;
        this.mContext = context;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        GoodsInfo goodsInfo = mGoodsList.get(position);
        MobileFragment fragment = MobileFragment.newInstance(position, goodsInfo.pic, goodsInfo.desc);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return mGoodsList.size();
    }
}
