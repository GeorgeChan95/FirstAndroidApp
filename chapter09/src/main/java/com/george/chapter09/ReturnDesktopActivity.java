package com.george.chapter09;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PictureInPictureParams;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;

public class ReturnDesktopActivity extends AppCompatActivity {
    public static final String TAG = "GeorgeTag";

    private DesktopRecevier desktopRecevier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_desktop);
        desktopRecevier = new DesktopRecevier();
        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(desktopRecevier, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(desktopRecevier);
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        if (isInPictureInPictureMode) {
            Log.d(TAG, "进入画中画模式");
        } else {
            Log.d(TAG, "退出画中画模式");
        }
    }

    private class DesktopRecevier extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra("reason");
                if (reason.length() > 0 && (reason.equals("homekey") || reason.equals("recentappps"))) {
                    // Android 8.0开始才提供画中画模式
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !isInPictureInPictureMode()) {
                        // 创建画中画模式的参数构建器
                        PictureInPictureParams.Builder builder = new PictureInPictureParams.Builder();
                        // 设置宽高比例值，第一个参数表示分子，第二个参数表示分母
                        // 下面的10/5=2，表示画中画窗口的宽度是高度的两倍
                        // 设置画中画窗口的宽高比例
                        Rational rational = new Rational(10, 5);
                        builder.setAspectRatio(rational);
                        // 进入画中画模式
                        enterPictureInPictureMode(builder.build());
                    }
                }
            }
        }
    }
}