package com.george.chapter09.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.george.chapter09.BindImmediateActivity;
import com.george.chapter09.ServiceDataActivity;

public class DataService extends Service {
    private static final String TAG = "GeorgeTag";
    public DataService() {
    }

    private final IBinder mBinder = new LocalBinder();
    public class LocalBinder extends Binder {
        public DataService getService() {
            return DataService.this;
        }

        public int getNumber(int number) {
            Log.d(TAG, "DataService接收到参数：" + number);
            return 2*number;
        }
    }

    private void refresh(String text) {
        ServiceDataActivity.showText(text);
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