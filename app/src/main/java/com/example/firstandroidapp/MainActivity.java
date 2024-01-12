package com.example.firstandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("启动成功：{}", "Hello Android");
        Log.d("MainActivity", "这是tag");
        Log.e("MainActivity", "这是另一个日志");
    }
}