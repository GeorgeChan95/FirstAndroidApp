package com.george.chapter14;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.george.chapter14.utils.BitmapUtil;

public class PhotoDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        ImageView iv_photo = findViewById(R.id.iv_photo);
        String photo_path = getIntent().getStringExtra("photo_path");
        Bitmap bitmap = BitmapFactory.decodeFile(photo_path);
        iv_photo.setImageBitmap(BitmapUtil.getAutoZoomImage(bitmap));
    }
}