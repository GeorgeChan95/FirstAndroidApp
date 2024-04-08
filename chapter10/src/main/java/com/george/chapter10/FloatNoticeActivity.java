package com.george.chapter10;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.george.chapter10.interfaces.FloatClickListener;
import com.george.chapter10.util.Utils;
import com.george.chapter10.widget.FloatWindow;

public class FloatNoticeActivity extends AppCompatActivity {
    private static final String TAG = "GeorgeTag";

    private static FloatWindow mFloatWindow;
    private EditText et_content;
    private TextView tv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float_notice);
        et_content = findViewById(R.id.et_content);
        findViewById(R.id.btn_open_float).setOnClickListener(v -> openFloatWindow());
        findViewById(R.id.btn_close_float).setOnClickListener(v -> closeFloatWindow());
        // 判断权限、权限申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Log.d(TAG, "当前应用没有窗口悬浮权限，跳转到设置页面进行设置");
            // 悬浮窗权限未获取
            jumpToOverlay();
        }
    }

    /**
     * 打开悬浮窗
     */
    private void openFloatWindow() {
        Log.d(TAG, "执行了 openFloatWindow");
        if (mFloatWindow == null) {
            // 创建一个新的悬浮窗
            mFloatWindow = new FloatWindow(MainApplication.getInstance());
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
                    Intent intent = new Intent(FloatNoticeActivity.this, CustomButtonActivity.class);
                    startActivity(intent);
                    // 关闭悬浮窗
                    mFloatWindow.close();
                }
            });
        }

        if (mFloatWindow != null || !mFloatWindow.isShow()) {
            tv_content.setText(et_content.getText());
            mFloatWindow.show(Gravity.LEFT|Gravity.TOP);
        }
    }

    /**
     * 关闭悬浮窗
     */
    private void closeFloatWindow() {
        if (mFloatWindow != null && mFloatWindow.isShow()) {
            Log.d(TAG, "执行了 closeFloatWindow");
            // 关闭悬浮窗
            mFloatWindow.close();
        }
    }

    /**
     * 跳转到 => 应用的系统设置页面,允许悬浮
     */
    private void jumpToOverlay() {
        // 跳转到系统设置页面
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
}