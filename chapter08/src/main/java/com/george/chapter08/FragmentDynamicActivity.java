package com.george.chapter08;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;

import com.george.chapter08.adapter.MobilePagerAdapter;
import com.george.chapter08.entity.GoodsInfo;

import java.util.ArrayList;

public class FragmentDynamicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_dynamic);

        // 初始化标签栏
        initPageStrip();
        // 初始化翻页视图
        initViewPager();
    }

    private void initPageStrip() {
        PagerTabStrip pts_tab = findViewById(R.id.pts_tab);
        pts_tab.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        pts_tab.setTextColor(Color.BLACK);
    }

    /**
     * 初始化翻页视图
     */
    private void initViewPager() {
        ViewPager vp_content = findViewById(R.id.vp_content);
        ArrayList<GoodsInfo> goodList = GoodsInfo.getDefaultList();
        MobilePagerAdapter adapter = new MobilePagerAdapter(getSupportFragmentManager(), goodList);
        vp_content.setAdapter(adapter);
    }
}