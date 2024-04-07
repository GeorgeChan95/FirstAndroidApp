package com.george.chapter10;

import android.app.Application;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.george.chapter10.util.NotifyUtil;

/**
 * 在应用生命周期中，创建通知渠道
 */
public class MainApplication extends Application {
    private final static String TAG = "GeorgeTag";
    private static MainApplication mApp;

    private static MainApplication getInstance() {
        return mApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8.0开始必须给每个通知分配对应的渠道
            NotifyUtil.createNotifyChannel(this, getString(R.string.app_name), getString(R.string.app_name), NotificationManager.IMPORTANCE_LOW);
        }
        Log.d(TAG, "MainApplication, onCreate ...");
        // 在打开应用时对静态的应用实例赋值
        mApp = this;
    }
}
