package com.george.chapter03;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.george.chapter03.utils.DateUtils;

/**
 * 监听单个按钮的点击事件
 */
public class ButtonClickSingleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_click_single);
        /**
         * 2、给单个监听的按钮，设置监听点击事件的类
         */
        Button btnSingle = findViewById(R.id.btn_single_click);
        btnSingle.setOnClickListener(new MySingleClick());
    }

    /**
     * 3、定义监听点击事件的类，实现 View.OnClickListener 接口，并重写 onClick() 方法
     */
    private class MySingleClick implements View.OnClickListener {
        /**
         *
         * @param v 表示监听的对象Button
         */
        @Override
        public void onClick(View v) {
            String time = DateUtils.getNowTime();
            String desc = String.format("%s 你点击了按钮：%s", time, ((Button)v).getText());

            TextView singleView = findViewById(R.id.tv_result_single);
            singleView.setText(desc);
        }
    }
}