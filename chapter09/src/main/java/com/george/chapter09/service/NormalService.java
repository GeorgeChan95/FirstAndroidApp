package com.george.chapter09.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.george.chapter09.ServiceNormalActivity;

public class NormalService extends Service {
    private static final String TAG = "GeorgeTag";
    public NormalService() {
    }

    private void refresh(String text) {
        ServiceNormalActivity.showText(text);
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "执行方法onCreate...");
        super.onCreate();
        refresh("onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "执行方法onStartCommand...");
        refresh("onStartCommand");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "执行方法onBind...");
        refresh("onBind");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "执行方法onUnbind...");
        refresh("onUnbind");
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "执行方法onRebind...");
        super.onRebind(intent);
        refresh("onRebind");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "执行方法onDestroy...");
        super.onDestroy();
        refresh("onRebind");
    }

}