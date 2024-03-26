package com.george.chapter09.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

public class StaticReceiver extends BroadcastReceiver {
    private static final String TAG = "GeorgeTag";
    public static final String STATIC_ACTION = "com.george.chapter09.staticAction";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction().equals(STATIC_ACTION)) {
            Log.d(TAG, "接收到静态广播");
            // 从系统服务中获取震动管理器
            Vibrator vb = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vb.vibrate(500); // 命令震动器吱吱个若干秒，这里的500表示500毫秒
        }
    }
}