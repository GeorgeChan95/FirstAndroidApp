package com.george.chapter04;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class FinishActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        findViewById(R.id.btn_finish_img).setOnClickListener(this);
        findViewById(R.id.btn_finish_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_finish_img || v.getId() == R.id.btn_finish_btn) {
            // 调用finish()方法，关闭当前页面，返回上一个页面
            finish();
        }
    }

    @Override
    protected void onStart() {
        Log.d("finishTag", "调用了onStart()方法");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d("finishTag", "调用了onResume()方法");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d("finishTag", "调用了onStop()方法");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("finishTag", "调用了onDestroy()方法");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // finish()方法的调用，不会触发页面的 onPause() 方法
        Log.d("finishTag", "调用了onPause()方法");
        super.onPause();
    }
}