package com.george.chapter09.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.george.chapter09.BindDelayActivity;

public class BindDelayService extends Service {
    private static final String TAG = "GeorgeTag";

    // 创建一个粘合剂对象
    private final IBinder mBinder = new LocalBinder();
    // 定时Service粘合剂，用于将该服务粘合到活动页面的进程中
    public class LocalBinder extends Binder {
        public BindDelayService getService() {
            return BindDelayService.this;
        }
    }

    public BindDelayService() {
    }

    private void refresh(String text) {
        BindDelayActivity.showText(text);
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
        return mBinder;
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
        refresh("onDestroy");
    }
}