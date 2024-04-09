package com.george.chapter11;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.george.chapter11.adapter.TabPagerAdapter;

public class TabPagerActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {

    // 翻页视图对象
    private ViewPager vp_content;
    // 单选组对象
    private RadioGroup rg_tabbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_pager);
        vp_content = findViewById(R.id.vp_content);
        rg_tabbar = findViewById(R.id.rg_tabbar);

        TabPagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager());
        vp_content.setAdapter(adapter);
        // 给翻页视图添加页面变更监听器
        vp_content.addOnPageChangeListener(this);

        rg_tabbar.setOnCheckedChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        // 对应下表的RadioButton选中
        rg_tabbar.check(rg_tabbar.getChildAt(position).getId());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int position = 0; position < rg_tabbar.getChildCount(); position++) {
            // 获得指定位置的单选按钮
            RadioButton radio = (RadioButton) rg_tabbar.getChildAt(position);
            // 如果当前单选按钮正是选中的按钮，则设置翻页视图到当前下标
            if (radio.getId() == checkedId) {
                vp_content.setCurrentItem(position);
            }
        }
    }
}