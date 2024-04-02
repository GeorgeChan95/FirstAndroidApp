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

import com.george.chapter09.service.BindDelayService;
import com.george.chapter09.util.DateUtil;

/**
 * Service延迟绑定
 */
public class BindDelayActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "GeorgeTag";

    private static TextView tv_delay;
    // 绑定了Service的意图对象
    private Intent intent;
    private static String mDesc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_delay);
        tv_delay = findViewById(R.id.tv_delay);

        // 启动服务
        findViewById(R.id.btn_start).setOnClickListener(this);
        // 绑定服务
        findViewById(R.id.btn_bind).setOnClickListener(this);
        // 解绑服务
        findViewById(R.id.btn_unbind).setOnClickListener(this);
        // 停止服务
        findViewById(R.id.btn_stop).setOnClickListener(this);
        // 创建一个通往普通服务的意图
        intent = new Intent(this, BindDelayService.class);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start) {
            startService(intent);
            Log.d(TAG, "startService...");
        } else if (v.getId() == R.id.btn_bind) {
            // 绑定服务。如果服务未启动，则系统先启动该服务再进行绑定
            boolean bindFlag = bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
            Log.d(TAG, "bindFlag=" + bindFlag);
        } else if (v.getId() == R.id.btn_unbind) {
            if (delayService != null) {
                // 解绑服务。如果先前服务立即绑定，则此时解绑之后自动停止服务,如果启动服务和绑定服务分两次执行，则在此只执行解绑，不会停止服务。
                unbindService(mServiceConnection);
            }
        } else if (v.getId() == R.id.btn_stop) {
            stopService(intent);
            Log.d(TAG, "stopService...");
        }
    }

    private BindDelayService delayService = null;
    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            delayService = ((BindDelayService.LocalBinder) service).getService();
            Log.d(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            delayService = null;
            Log.d(TAG, "onServiceDisconnected");
        }
    };

    public static void showText(String desc) {
        if (tv_delay != null) {
            mDesc = String.format("%s%s %s\n", mDesc, DateUtil.getNowDateTime(), desc);
            tv_delay.setText(mDesc);
        }
    }
}