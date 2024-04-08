package com.george.chapter10.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.george.chapter10.CustomButtonActivity;
import com.george.chapter10.R;
import com.george.chapter10.interfaces.FloatClickListener;
import com.george.chapter10.util.Utils;
import com.george.chapter10.widget.FloatWindow;

/**
 * 悬浮窗服务
 */
public class FloatingWindowService extends Service {
    private static final String TAG = "rfDevFloatingService";

    private String message;
    private static FloatWindow mFloatWindow;
    private TextView tv_content;

    // 创建一个粘合剂对象
    private final IBinder mBinder = new LocalBinder();

    private class LocalBinder extends Binder {
        public FloatingWindowService getService() {
            return FloatingWindowService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 获取输入的参数
        message = intent.getStringExtra("message");
        if (mFloatWindow == null) {
            // 创建一个新的悬浮窗
            mFloatWindow = new FloatWindow(FloatingWindowService.this);
            // 设置悬浮窗的布局内容
            mFloatWindow.setLayout(R.layout.float_notice);
            tv_content = mFloatWindow.mContentView.findViewById(R.id.tv_content);
            LinearLayout ll_float = mFloatWindow.mContentView.findViewById(R.id.ll_float);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) ll_float.getLayoutParams();
            int margin = Utils.dip2px(this, 5);
            // 悬浮窗四周留白
            layoutParams.setMargins(margin, margin, margin, margin);
            // 向上居中
            layoutParams.gravity = Gravity.TOP|Gravity.CENTER;
            ll_float.setLayoutParams(layoutParams);
            // 设置悬浮窗点击监听
            mFloatWindow.setOnFloadClickListener(new FloatClickListener() {
                @Override
                public void onFloatClick(View v) {
                    // 跳转页面
                    Intent intent = new Intent(FloatingWindowService.this, CustomButtonActivity.class);
                    startActivity(intent);
                    // 关闭悬浮窗
                    mFloatWindow.close();
                }
            });
        }

        if (mFloatWindow != null || !mFloatWindow.isShow()) {
            tv_content.setText(message);
            mFloatWindow.show(Gravity.LEFT|Gravity.TOP);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mFloatWindow != null && mFloatWindow.isShow()) {
            Log.d(TAG, "执行了 closeFloatWindow");
            // 关闭悬浮窗
            mFloatWindow.close();
        }
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }
}