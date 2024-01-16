package com.george.chapter03;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.george.chapter03.utils.DateUtils;

public class ButtonEnableActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_enable);

        // 因为按钮控件的setOnClickListener方法来源于View基类，
        // 所以也可对findViewById得到的视图直接设置点击监听器
        findViewById(R.id.btn_enable).setOnClickListener(this);
        findViewById(R.id.btn_disable).setOnClickListener(this);
        findViewById(R.id.btn_test).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Button btnTest = findViewById(R.id.btn_test);
        if (v.getId() == R.id.btn_enable) {
            btnTest.setEnabled(true);
            btnTest.setTextColor(Color.BLACK);
        } else if (v.getId() == R.id.btn_disable) {
            btnTest.setEnabled(false);
            btnTest.setTextColor(Color.GRAY);
        } else if (v.getId() == R.id.btn_test) {
            TextView textView = findViewById(R.id.tv_result_test);
            String time = DateUtils.getNowTime();
            String desc = String.format("%s 你点击了按钮：%s", time, ((Button)v).getText());
            textView.setText(desc);
        }
    }
}