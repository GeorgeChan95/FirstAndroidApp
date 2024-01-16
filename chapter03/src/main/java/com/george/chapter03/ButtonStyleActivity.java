package com.george.chapter03;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.george.chapter03.utils.DateUtils;

public class ButtonStyleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_style);
    }

    /**
     * 在 activity_button_style.xml 中直接指定的按钮点击方法：doClick
     * @param view
     */
    public void doClick(View view) {
        Button button = findViewById(R.id.tv_button1);
        String time = DateUtils.getNowTime();
        String desc = String.format("%s 你点击了按钮：%s", time, button.getText());

        TextView result = findViewById(R.id.tv_button_result);
        result.setText(desc);
    }
}