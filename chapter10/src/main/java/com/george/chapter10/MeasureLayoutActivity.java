package com.george.chapter10;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.george.chapter10.util.MeasureUtil;

public class MeasureLayoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_layout);
        LinearLayout ll_header = findViewById(R.id.ll_header);
        TextView tv_desc = findViewById(R.id.tv_desc);
        // 计算获取线性布局的实际高度
        float height = MeasureUtil.getRealHeight(ll_header);
        String desc = String.format("上面下拉刷新头部的高度是%f", height);
        tv_desc.setText(desc);
    }
}