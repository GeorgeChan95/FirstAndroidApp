package com.george.chapter14;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.george.chapter14.listener.DurationSelectedListener;
import com.george.chapter14.listener.EncoderSelectedListener;
import com.george.chapter14.listener.FormatSelectedListener;
import com.george.chapter14.utils.MediaUtil;
import com.george.chapter14.utils.PermissionUtil;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MediaRecorderActivity extends AppCompatActivity implements MediaRecorder.OnInfoListener {
    private String TAG = "GeorgeTag";

    private MediaRecorderActivity mRecorder;
    private Button btn_record;
    private LinearLayout ll_progress;
    private ProgressBar pb_record;
    private TextView tv_progress;
    private ImageView iv_audio;
    private boolean isRecording = false; // 是否正在录制
    private int mAudioEncoder; // 音频编码
    private int mOutputFormat; // 输出格式
    private int mDuration; // 录制时长
    private Timer mTimer = new Timer(); // 计时器
    private int mTimeCount; // 时间计数
    private String mRecordFilePath; // 录制文件的保存路径
    private MediaRecorder mMediaRecorder = new MediaRecorder(); // 媒体录制器
    private MediaPlayer mMediaPlayer = new MediaPlayer(); // 媒体播放器

    public String[] PERMISSIONS = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_recorder);
        // 开始录制按钮
        btn_record = findViewById(R.id.btn_record);
        // 进度条布局容器
        ll_progress = findViewById(R.id.ll_progress);
        // 进度条
        pb_record = findViewById(R.id.pb_record);
        // 进度条文字
        tv_progress = findViewById(R.id.tv_progress);
        // 播放按钮控件
        iv_audio = findViewById(R.id.iv_audio);
        // 开启录制按钮点击监听
        btn_record.setOnClickListener(v -> {
            if (!isRecording) { // 当前不在录制中
                startRecord(); // 开启录制
            } else {
                stopRecord(); // 停止录制
            }
        });
        // 播放按钮监听，点击就开始播放录音
        iv_audio.setOnClickListener(v -> playAudio());

        mRecorder = new MediaRecorderActivity();
        // 初始化音频编码下拉框
        initEncoderSpinner(mRecorder);
        // 初始化输出格式下拉框
        initFormatSpinner(mRecorder);
        // 初始化录制时长的下拉框
        initDurationSpinner(mRecorder);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 打开页面，立即校验权限
        PermissionUtil.checkPermission(this, PERMISSIONS, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (PermissionUtil.checkGrant(grantResults)) {
                    Log.d(TAG, "权限获取成功");
                } else {
                    Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show();
                    jumpToSettings();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 初始化音频编码下拉框
     * @param activity 类实例对象
     */
    private void initEncoderSpinner(MediaRecorderActivity activity) {
        // 声明数组适配器
        ArrayAdapter<String> encoderAdapter = new ArrayAdapter<>(this, R.layout.item_select, encoderDescArray);
        Spinner sp_encoder = findViewById(R.id.sp_encoder);
        sp_encoder.setPrompt("请选择音频编码"); // 设置下拉框标题
        sp_encoder.setSelection(0); // 设置默认选中第1个选项
        sp_encoder.setAdapter(encoderAdapter); // 设置适配器
        // 设置下拉框切换选中监听器，选中其中一项则触发监听器的 onItemSelected 方法
        sp_encoder.setOnItemSelectedListener(new EncoderSelectedListener(activity, encoderArray));
    }

    /**
     * 初始化输出格式下拉框
     * @param activity 类实例对象
     */
    private void initFormatSpinner(MediaRecorderActivity activity) {
        // 声明数组适配器
        ArrayAdapter<String> formatAdapter = new ArrayAdapter<>(this, R.layout.item_select, formatDescArray);
        Spinner sp_format = findViewById(R.id.sp_format);
        sp_format.setPrompt("请选择输出格式"); // 设置下拉框标题
        sp_format.setSelection(0); // 设置默认选中第1个选项
        sp_format.setAdapter(formatAdapter); // 设置适配器
        // 设置下拉框切换选中监听器，选中其中一项则触发监听器的 onItemSelected 方法
        sp_format.setOnItemSelectedListener(new FormatSelectedListener(activity, formatArray));
    }

    /**
     * 初始化录制时长的下拉框
     * @param activity
     */
    private void initDurationSpinner(MediaRecorderActivity activity) {
        // 声明数组适配器
        ArrayAdapter<String> durationAdapter = new ArrayAdapter<>(this, R.layout.item_select, durationDescArray);
        Spinner sp_duration = findViewById(R.id.sp_duration);
        sp_duration.setPrompt("请选择录制时长"); // 设置下拉框标题
        sp_duration.setSelection(0); // 设置默认选中第1个选项
        sp_duration.setAdapter(durationAdapter); // 设置适配器
        // 设置下拉框切换选中监听器，选中其中一项则触发监听器的 onItemSelected 方法
        sp_duration.setOnItemSelectedListener(new DurationSelectedListener(activity, durationArray));
    }

    /**
     * 开启录音
     */
    @SuppressLint("WrongConstant")
    private void startRecord() {
        // 将进度条显示出来
        ll_progress.setVisibility(View.VISIBLE);
        isRecording = true;
        btn_record.setText("停止录制");
        pb_record.setMax(mRecorder.getmDuration());
        mTimeCount = 0; // 时间计数清零
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 设置进度条当前进度
                pb_record.setProgress(mTimeCount);
                // 格式化时间，展示时间进度
                tv_progress.setText(MediaUtil.formatDuration(mTimeCount * 1000));
                // 秒数+1
                mTimeCount++;
            }
        }, 0, 1000); // 计时器每隔一秒就更新录制进度
        // 本次录制的文件路径
        mRecordFilePath = MediaUtil.getRecordFilePath(this, "RecordAudio", ".amr");

        // 录制音频相关代码
        mMediaRecorder.reset(); // 重置媒体录制器
        mMediaRecorder.setOnInfoListener(this); // 设置录制器的监听
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 设置音频源为：麦克风
        mMediaRecorder.setOutputFormat(mRecorder.getmOutputFormat()); // 设置媒体输出格式，该方法要先于 setAudioEncoder调用
        mMediaRecorder.setAudioEncoder(mRecorder.getmAudioEncoder()); // 设置音频编码器
//        mMediaRecorder.setAudioSamplingRate(8); // 设置媒体的音频采样率（可选）
//        mMediaRecorder.setAudioChannels(2); // 设置媒体音频声道数（可选）
//        mMediaRecorder.setAudioEncodingBitRate(1024); // 设置音频每秒录制的字节数（可选）
        mMediaRecorder.setMaxDuration(mRecorder.getmDuration() * 1000); // 设置录制的最大时长
//        mMediaRecorder.setMaxFileSize(1024*1024*10); // 设置媒体的最大文件大小
        // setMaxFileSize与setMaxDuration设置其一即可
        mMediaRecorder.setOutputFile(mRecordFilePath); // 设置媒体文件的保存路径
        try {
            mMediaRecorder.prepare(); // 媒体录制器准备就绪
            mMediaRecorder.start(); // 媒体录制器开始录制
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录音
     */
    private void stopRecord() {
        isRecording = false;
        btn_record.setText("开始录制");
        // 取消定时器
        mTimer.cancel();
        mMediaRecorder.stop(); // 媒体录制器停止录制
    }

    /**
     * 播放录音
     */
    private void playAudio() {
        mMediaPlayer.reset(); // 重置媒体播放器
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); // 设置音频流的类型为音乐
        try {
            // 设置媒体数据的文件路径
            mMediaPlayer.setDataSource(mRecordFilePath);
            mMediaPlayer.prepare(); // 媒体播放器准备就绪
            mMediaPlayer.start(); // 媒体播放器开始播放
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 媒体录制器监听回调方法
     * @param mr
     * @param what
     * @param extra
     */
    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        // 当录制达到了最大时长，或者达到了文件大小的限制，都停止录制
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED
                || what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
            // 停止录制
            stopRecord();
            // 显示进度条
            iv_audio.setVisibility(View.VISIBLE);
            // 弹出提示
            Toast.makeText(this, "已结束录制，音频文件路径为：" + mRecordFilePath, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 页面停止活动时，停止录音，停止播放
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (!TextUtils.isEmpty(mRecordFilePath) && isRecording) {
            // 停止录制录音
            stopRecord();
        }
        if (mMediaPlayer.isPlaying()) { // 如果正在播放
            mMediaPlayer.stop(); // 停止播放
        }
    }

    /**
     * 页面销毁时，释放相关资源
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaRecorder.release(); // 释放媒体录制器
        mMediaPlayer.release(); // 视频媒体播放器
    }

    /**
     * 音频编码格式
     */
    private String[] encoderDescArray = {
            "默认编码",
            "窄带编码",
            "宽带编码",
            "低复杂度的高级编码",
            "高效率的高级编码",
            "增强型低延时的高级编码"
    };
    private int[] encoderArray = {
            MediaRecorder.AudioEncoder.DEFAULT,
            MediaRecorder.AudioEncoder.AMR_NB,
            MediaRecorder.AudioEncoder.AMR_WB,
            MediaRecorder.AudioEncoder.AAC,
            MediaRecorder.AudioEncoder.HE_AAC,
            MediaRecorder.AudioEncoder.AAC_ELD
    };

    /**
     * 音频输出格式
     */
    private String[] formatDescArray = {
            "默认格式",
            "窄带格式",
            "宽带格式",
            "高级的音频传输流格式"
    };
    private int[] formatArray = {
            MediaRecorder.OutputFormat.DEFAULT,
            MediaRecorder.OutputFormat.AMR_NB,
            MediaRecorder.OutputFormat.AMR_WB,
            MediaRecorder.OutputFormat.AAC_ADTS
    };

    /**
     * 音频录制时长
     */
    private String[] durationDescArray = {"5秒", "10秒", "20秒", "30秒", "60秒"};
    private int[] durationArray = {5, 10, 20, 30, 60};

    /**
     * 设置音频编码
     * @param mAudioEncoder
     */
    public void setmAudioEncoder(int mAudioEncoder) {
        this.mAudioEncoder = mAudioEncoder;
    }

    public int getmAudioEncoder() {
        return mAudioEncoder;
    }

    /**
     * 设置输出格式编码
     * @param mOutputFormat
     */
    public void setmOutputFormat(int mOutputFormat) {
        this.mOutputFormat = mOutputFormat;
    }

    public int getmOutputFormat() {
        return mOutputFormat;
    }

    /**
     * 设置录制时长
     * @param mDuration
     */
    public void setmDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public int getmDuration() {
        return mDuration;
    }

    /**
     * 跳转到 => 应用的系统设置页面
     */
    private void jumpToSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}