package com.george.chapter07_client;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.george.chapter07_client.observer.SmsGetObserver;

public class MonitorSmsActivity extends AppCompatActivity {
    private String TAG = "GeorgeTag";

    private SmsGetObserver observer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_sms);
        // 给指定Uri注册内容观察器，一旦发生数据变化，就触发观察器的onChange方法
        Uri uri = Uri.parse("content://sms");

        // notifyForDescendents：
        // false ：表示精确匹配，即只匹配该Uri，true ：表示可以同时匹配其派生的Uri
        // 假设UriMatcher 里注册的Uri共有一下类型：
        // 1.content://AUTHORITIES/table
        // 2.content://AUTHORITIES/table/#
        // 3.content://AUTHORITIES/table/subtable
        // 假设我们当前需要观察的Uri为content://AUTHORITIES/student:
        // 如果发生数据变化的 Uri 为 3。
        // 当notifyForDescendents为false，那么该ContentObserver会监听不到，但是当notifyForDescendents 为ture，能捕捉该Uri的数据库变化。
        observer = new SmsGetObserver(this);
        getContentResolver().registerContentObserver(uri, true, observer);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 活动销毁时，取消注册监听器
        getContentResolver().unregisterContentObserver(observer);
    }
}