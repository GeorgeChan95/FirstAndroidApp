package com.george.chapter02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        TextView hello = findViewById(R.id.tv_hello);
        String text = hello.getText().toString();
        text = text + " 追加内容";
        hello.setText(text);
        hello.setTextSize(25);
    }
}