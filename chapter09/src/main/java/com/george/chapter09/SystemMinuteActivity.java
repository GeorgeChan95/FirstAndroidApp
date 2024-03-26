package com.george.chapter09;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.george.chapter09.receiver.TimeReceiver;

/**
 * 分钟到达广播，必须跳转到当前Activity对应的页面才有效
 */
public class SystemMinuteActivity extends AppCompatActivity {

    private TimeReceiver timeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_minute);
    }

    @Override
    protected void onStart() {
        super.onStart();
        timeReceiver = new TimeReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        registerReceiver(timeReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(timeReceiver);
    }
}