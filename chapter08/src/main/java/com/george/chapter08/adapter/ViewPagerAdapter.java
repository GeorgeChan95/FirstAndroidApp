package com.george.chapter08.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.george.chapter08.entity.GoodsInfo;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<GoodsInfo> goodsList;

    // 声明一个图像视图列表
    private List<ImageView> mViewList = new ArrayList<>();

    public ViewPagerAdapter(Context mContext, List<GoodsInfo> goodsList) {
        this.mContext = mContext;
        this.goodsList = goodsList;

        // 给每个商品分配一个专用的图像视图
        for (GoodsInfo goods : goodsList) {
            ImageView imageView = new ImageView(mContext);
            imageView.setImageResource(goods.pic);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // 添加到图像列表中
            mViewList.add(imageView);
        }
    }

    @Override
    public int getCount() {
        return goodsList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    /**
     * 实例化指定位置的页面，并将其添加到容器中
     *
     * 添加一个view到container中，而后返回一个跟这个view可以关联起来的对象，
     * 这个对象能够是view自身，也能够是其余对象，
     * 关键是在isViewFromObject可以将view和这个object关联起来
     * @param container
     * @param position
     * @return
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = mViewList.get(position);
        container.addView(imageView);
        return imageView;
    }

    /**
     * 从容器中销毁指定位置的页面
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(mViewList.get(position));
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return goodsList.get(position).name;
    }
}
