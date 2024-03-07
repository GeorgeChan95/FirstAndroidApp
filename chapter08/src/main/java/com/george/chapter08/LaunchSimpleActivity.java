package com.george.chapter08;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.george.chapter08.adapter.LaunchSimpleAdapter;

public class LaunchSimpleActivity extends AppCompatActivity {
    // 声明引导页面的图片数组
    private int[] lanuchImageArray = {R.drawable.guide_bg1,
            R.drawable.guide_bg2, R.drawable.guide_bg3, R.drawable.guide_bg4};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_simple);

        ViewPager vp_content = findViewById(R.id.vp_content);
        LaunchSimpleAdapter adapter = new LaunchSimpleAdapter(this, lanuchImageArray);
        vp_content.setAdapter(adapter);
    }
}