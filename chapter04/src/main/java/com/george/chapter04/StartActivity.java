package com.george.chapter04;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        findViewById(R.id.btn_finish).setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_finish) {
            Intent intent = new Intent(StartActivity.this, FinishActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(new Intent(StartActivity.this, FinishActivity.class));
        }
    }
}