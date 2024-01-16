package com.george.chapter03;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.george.chapter03.utils.DateUtils;

/**
 * 页面按钮公共的监听器
 * 1、当前页面实现接口 View.OnClickListener
 */
public class ButtonClickPublicActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_click_public);

        // 4、调用按钮对象的 setOnClickListener()方法
        Button btnPublic = findViewById(R.id.btn_public_click);
        btnPublic.setOnClickListener(this);
    }

    /**
     * 3、重写点击事件公用的处理方法
     * @param v
     */
    @Override
    public void onClick(View v) {
        // 根据Button的Id区分
        if (v.getId() == R.id.btn_public_click) {
            String time = DateUtils.getNowTime();
            String desc = String.format("%s 你点击了按钮：%s", time, ((Button)v).getText());
            TextView result = findViewById(R.id.tv_result_public);
            result.setText(desc);
        }

    }
}