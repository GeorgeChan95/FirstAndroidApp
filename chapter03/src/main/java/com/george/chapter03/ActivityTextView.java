package com.george.chapter03;

import static android.util.TypedValue.COMPLEX_UNIT_PX;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityTextView extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);
        TextView tv_hello_id = findViewById(R.id.tv_hello);
        // 设置文件内容
        tv_hello_id.setText("你好，安卓");
        // 设置文本字体大小
        tv_hello_id.setTextSize(COMPLEX_UNIT_PX, 50);
    }
}
