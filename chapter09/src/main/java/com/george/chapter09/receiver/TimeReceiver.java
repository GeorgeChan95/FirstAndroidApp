package com.george.chapter09.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TimeReceiver extends BroadcastReceiver {
    public static final String TAG = "GeorgeTag";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
            Log.d(TAG, "收到一个分钟到达广播");
        }
    }
}
