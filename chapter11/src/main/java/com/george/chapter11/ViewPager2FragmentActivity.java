package com.george.chapter11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.george.chapter11.adapter.MobilePagerAdapter;
import com.george.chapter11.entity.GoodsInfo;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class ViewPager2FragmentActivity extends AppCompatActivity {

    private List<GoodsInfo> mGoodsList = GoodsInfo.getDefaultList(); // 商品信息列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager2_fragment);
        // 从布局文件中获取一个 TabLayout布局
        TabLayout tab_title = findViewById(R.id.tab_title);
        // 获取ViewPager2布局
        ViewPager2 vp2_content = findViewById(R.id.vp2_content);
        // 构建一个商品信息的翻页适配器
        MobilePagerAdapter adapter = new MobilePagerAdapter(this, mGoodsList, this);
        vp2_content.setAdapter(adapter);

        // 把标签布局和翻页视图
        new TabLayoutMediator(tab_title, vp2_content, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(mGoodsList.get(position).desc);
            }
        }).attach();
    }
}