package com.george.chapter10;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;
import com.george.chapter10.service.FloatingWindowService;

public class FloatNoticeActivity extends AppCompatActivity {
    private static final String TAG = "GeorgeTag";

    private EditText et_content;

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
        FloatingWindowService service = new FloatingWindowService();
        service.setMContext(FloatNoticeActivity.this);
        Intent intent = new Intent(this, FloatingWindowService.class);
        intent.putExtra("message", et_content.getText().toString());
        startService(intent);
    }

    /**
     * 关闭悬浮窗
     */
    private void closeFloatWindow() {
        Intent intent = new Intent(this, FloatingWindowService.class);
        stopService(intent);
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