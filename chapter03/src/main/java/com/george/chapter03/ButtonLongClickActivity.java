package com.george.chapter03;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.george.chapter03.utils.DateUtils;

public class ButtonLongClickActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_long_click);

        Button button = findViewById(R.id.btn_long_click);
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (v.getId() == R.id.btn_long_click) {
                    TextView textView = findViewById(R.id.tv_result_long);
                    String time = DateUtils.getNowTime();
                    String desc = String.format("%s 你点击了按钮：%s", time, ((Button)v).getText());
                    textView.setText(desc);
                }
                return true;
            }
        });
    }
}