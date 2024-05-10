package com.george.chapter14;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Toast;

import com.george.chapter14.widget.CameraXView;

@SuppressLint("NewApi")
public class CameraxRecordActivity extends AppCompatActivity {
    private final static String TAG = "GeorgeTag";

    private CameraXView cxv_preview; // 声明一个增强相机视图对象
    private Chronometer chr_cost; // 声明一个计时器对象
    private ImageView iv_record; // 录制按钮对象
    private boolean isRecording = false; // 是否正在录像

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerax_record);
        // 初始化视图
        initView();
        // 初始化相机
        initCamera();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        cxv_preview = findViewById(R.id.cxv_preview);
        chr_cost = findViewById(R.id.chr_cost);
        iv_record = findViewById(R.id.iv_record);
        // 点击按钮，开始录像
        iv_record.setOnClickListener(v -> {
            // 处理录像动作
            dealRecord();
        });
        // 点击切换摄像头
        findViewById(R.id.iv_switch).setOnClickListener(v -> cxv_preview.switchCamera());
        findViewById(R.id.btn_play).setOnClickListener(v -> {
            if (TextUtils.isEmpty(cxv_preview.getmVideoPath())) {
                Toast.makeText(this, "请先录像再观看视频", Toast.LENGTH_SHORT).show();
                return;
            }
            // 下面跳到视频播放界面
            Intent intent = new Intent(this, VideoDetailActivity.class);
            intent.putExtra("video_path", cxv_preview.getmVideoPath());
            startActivity(intent);
        });
    }

    /**
     * 处理录像动作
     */
    private void dealRecord() {
        if (!isRecording) {
            iv_record.setImageResource(R.drawable.record_stop);
            // 开始录像
            cxv_preview.startRecord(15);
            // 显示计时器
            chr_cost.setVisibility(View.VISIBLE);
            // 设置计时器的基准时间
            chr_cost.setBase(SystemClock.elapsedRealtime());
            // 开始计时
            chr_cost.start();
            isRecording = true;
        } else {
            iv_record.setEnabled(false);
            // 停止录像
            cxv_preview.stopRecord();
        }
    }

    /**
     * 初始化相机
     */
    private void initCamera() {
        // 打开增强相机，并指定停止录像监听器
        cxv_preview.openCamera(this, CameraXView.MODE_RECORD, result -> runOnUiThread(() -> {
            // 隐藏并停止计时器
            chr_cost.setVisibility(View.GONE);
            chr_cost.stop();
            iv_record.setImageResource(R.drawable.record_start);
            iv_record.setEnabled(true);
            isRecording = false;
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭相机
        cxv_preview.closeCamera();
    }
}