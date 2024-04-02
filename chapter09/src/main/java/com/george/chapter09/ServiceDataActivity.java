package com.george.chapter09;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.george.chapter09.service.DataService;
import com.george.chapter09.util.DateUtil;

public class ServiceDataActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "GeorgeTag";

    private static TextView tv_normal;
    // 绑定了Service的意图对象
    private Intent intent;
    private static String mDesc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_data);
        tv_normal = findViewById(R.id.tv_normal);

        // 启动服务
        findViewById(R.id.btn_bind).setOnClickListener(this);
        // 停止服务
        findViewById(R.id.btn_unbind).setOnClickListener(this);
        // 创建一个通往普通服务的意图
        intent = new Intent(this, DataService.class);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_bind) {
            // 绑定服务。如果服务未启动，则系统先启动该服务再进行绑定
            boolean bindFlag = bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            Log.d(TAG, "bindFlag=" + bindFlag);
        } else if (v.getId() == R.id.btn_unbind) {
            if (dataService != null) {
                // 解绑服务。如果先前服务立即绑定，则此时解绑之后自动停止服务
                unbindService(serviceConnection);
            }
        }
    }

    private DataService dataService = null;
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DataService.LocalBinder binder = (DataService.LocalBinder) service;
            dataService = binder.getService();
            int num = 100;
            Log.d(TAG, "向Service发送参数：" + num);
            showText("发送参数：" + num);
            int number = binder.getNumber(num);
            showText("收到Service响应参数：" + number);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            dataService = null;
        }
    };

    public static void showText(String desc) {
        if (tv_normal != null) {
            mDesc = String.format("%s%s %s\n", mDesc, DateUtil.getNowDateTime(), desc);
            tv_normal.setText(mDesc);
        }
    }
}