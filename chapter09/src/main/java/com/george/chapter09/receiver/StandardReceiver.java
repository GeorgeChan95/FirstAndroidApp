package com.george.chapter09.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * 广播接收器
 */
public class StandardReceiver extends BroadcastReceiver {
    private static final String TAG = "GeorgeTag";
    public static final String STANDARD_ACTION = "com.george.chapter09";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction().equals(STANDARD_ACTION)) {
            Log.d(TAG, "收到一个标准广播");
            Bundle extras = intent.getExtras();
            Integer data1 = (Integer) extras.get("key1");
            String data2 = (String) extras.get("key2");
            Log.d(TAG, "接收到参数：data1:" + data1 + "， data2:" + data2);
        }
    }
}