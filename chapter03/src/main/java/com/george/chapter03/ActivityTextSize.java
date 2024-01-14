package com.george.chapter03;

import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityTextSize extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_size);
        TextView px = findViewById(R.id.tv_px);
//        px.setText("111111");
        TextView dp = findViewById(R.id.tv_dp);
        TextView sp = findViewById(R.id.tv_sp);

        // 设置文本大小
        sp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
    }
}
