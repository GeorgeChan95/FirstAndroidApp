package com.george.chapter03;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ImageScaleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_scale);

        ImageView imgView = findViewById(R.id.tv_img_scale);
        imgView.setImageResource(R.drawable.apple);
        // 不设置，默认是：FIT_CENTER
        imgView.setScaleType(ImageView.ScaleType.CENTER);
    }
}