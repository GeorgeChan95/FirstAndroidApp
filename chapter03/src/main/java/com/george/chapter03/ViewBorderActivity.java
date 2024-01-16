package com.george.chapter03;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.george.chapter03.utils.Utils;

public class ViewBorderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_border);

        TextView borderView = findViewById(R.id.border_view);
        // 获取控件的布局参数
        ViewGroup.LayoutParams layoutParams = borderView.getLayoutParams();
        // 设置空间的宽度（设置px值）
        int pxValue = Utils.dip2px(this, 320);
        Log.d("ViewBorder", "转换成px:" + pxValue);
        layoutParams.width = pxValue;
        // 将设置好的布局参数，设置给控件
        borderView.setLayoutParams(layoutParams);
    }
}