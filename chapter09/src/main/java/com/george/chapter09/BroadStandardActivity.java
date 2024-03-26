package com.george.chapter09;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.george.chapter09.receiver.StandardReceiver;

/**
 * 标准广播
 */
public class BroadStandardActivity extends AppCompatActivity implements View.OnClickListener {
    private StandardReceiver standardReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broad_standard);
        findViewById(R.id.btn_send_standard).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // 发送标准广播
        Intent intent = new Intent(StandardReceiver.STANDARD_ACTION);
        Bundle bundle = new Bundle();
        bundle.putInt("key1", 1);
        bundle.putString("key2", "2");
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        standardReceiver = new StandardReceiver();
        // 创建一个意图过滤器，只处理 STANDARD_ACTION 的广播
        IntentFilter filter = new IntentFilter(StandardReceiver.STANDARD_ACTION);
        // 注册广播接收器
        registerReceiver(standardReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 卸载广播接收器
        unregisterReceiver(standardReceiver);
    }
}