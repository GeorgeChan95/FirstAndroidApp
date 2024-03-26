package com.george.chapter09;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.george.chapter09.receiver.StaticReceiver;

public class BroadStaticActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broad_static);
        findViewById(R.id.btn_send_static).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // Android8.0之后删除了大部分静态注册，防止退出App后仍在接收广播，
        // 为了让应用能够继续接收静态广播，需要给静态注册的广播指定包名。
        String receiverFullName = "com.george.chapter09.receiver.StaticReceiver";
        Intent intent = new Intent(StaticReceiver.STATIC_ACTION);
        // 发送静态广播之时，需要通过setComponent方法指定接收器的完整路径
        ComponentName componentName = new ComponentName(this, receiverFullName);
        // 设置意图信息组件
        intent.setComponent(componentName);
        sendBroadcast(intent);
    }
}