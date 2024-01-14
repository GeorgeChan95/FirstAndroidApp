package com.george.chapter03;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

public class TextColorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_color);
        TextView color1 = findViewById(R.id.tv_color1);
//        color1.setTextColor(Color.RED);
        // 六位编码的文字颜色，默认设置为透明的，透明就是看不到
//        color1.setTextColor(0x00ff00);
        // 八位编码文字颜色，显示设置了文字的透明度，ff表示完全不透明
//        color1.setTextColor(0xff00ff00);

        // 通过资源文件定义的颜色属性，设置文本颜色
        color1.setTextColor(getResources().getColor(R.color.black));

        // 设置背景颜色
        color1.setBackgroundColor(getResources().getColor(R.color.purple_700));
    }
}