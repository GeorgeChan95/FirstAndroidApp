package com.george.chapter09;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.george.chapter09.service.NormalService;
import com.george.chapter09.util.DateUtil;

/**
 * Service的启动与停止
 */
public class ServiceNormalActivity extends AppCompatActivity implements View.OnClickListener {

    private static TextView tv_normal;
    // 绑定了Service的意图对象
    private Intent intent;
    private static String mDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_normal);
        tv_normal = findViewById(R.id.tv_normal);

        // 启动服务
        findViewById(R.id.btn_start).setOnClickListener(this);
        // 停止服务
        findViewById(R.id.btn_stop).setOnClickListener(this);
        // 创建一个通往普通服务的意图
        intent = new Intent(this, NormalService.class);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start) {
            // 启动服务
            startService(intent);
        } else if (v.getId() == R.id.btn_stop) {
            // 停止服务
            stopService(intent);
        }
    }

    public static void showText(String desc) {
        if (tv_normal != null) {
            mDesc = String.format("%s%s %s\n", mDesc, DateUtil.getNowDateTime(), desc);
            tv_normal.setText(mDesc);
        }
    }
}