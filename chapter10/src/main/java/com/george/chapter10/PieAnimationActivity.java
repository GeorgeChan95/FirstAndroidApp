package com.george.chapter10;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.george.chapter10.widget.PieAnimation;

public class PieAnimationActivity extends AppCompatActivity implements View.OnClickListener {
    private PieAnimation pa_circle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_animation);
        pa_circle = findViewById(R.id.pa_circle);
        pa_circle.setOnClickListener(this);
        pa_circle.start();
    }

    @Override
    public void onClick(View v) {
        if (!pa_circle.isRunning) {
            pa_circle.start();
        } else {
            pa_circle.stop();
        }
    }
}