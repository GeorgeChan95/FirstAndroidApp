package com.george.chapter14;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.george.chapter14.constant.UrlConstant;
import com.george.chapter14.utils.PermissionUtil;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.util.MimeTypes;

import java.util.Locale;

public class ExoPlayerActivity extends AppCompatActivity {
    private final static String TAG = "GeorgeTag";
    // 声明一个新型播放器对象
    private ExoPlayer mPlayer;
    private final static String URL_HTTPS = "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4";
    private final static String URL_VIDEO = UrlConstant.HTTP_PREFIX + "海洋世界.mp4";
    private final static String URL_SUBTITLE = UrlConstant.HTTP_PREFIX + "海洋世界.srt";

    public String[] PERMISSIONS = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    public final int REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_player);
        // 播放视图控件
        StyledPlayerView pv_content = findViewById(R.id.pv_content);
        // 构建播放器对象
        mPlayer = new ExoPlayer.Builder(this).build();
        // 给播放器视图设置播放器对象
        pv_content.setPlayer(mPlayer);

        // 声明一个活动结果处理器，从手机中获取内容，然后进一步操作
        ActivityResultLauncher launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                // 播放视频
                playVideo(uri);
            }
        });
        // 选择本地视频播放
        findViewById(R.id.btn_play_local).setOnClickListener(v -> launcher.launch("video/*"));
        // 播放网络视频
        findViewById(R.id.btn_play_network).setOnClickListener(v -> playVideo(Uri.parse(URL_HTTPS)));
        // 播放带字幕的视频
        findViewById(R.id.btn_play_subtitle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri videoUri = Uri.parse(URL_VIDEO);
                Uri subtitleUri = Uri.parse(URL_SUBTITLE);
                // 播放带字幕的视频
                playVideoWithSubtitle(videoUri, subtitleUri);
            }
        });

        // 初始化迎宾曲下拉框
        initWelcomeSpinner();
    }

    private long mCurrentPosition = 0; // 当前的播放位置

    @Override
    protected void onResume() {
        super.onResume();
        // 打开页面，立即校验权限
        PermissionUtil.checkPermission(this, PERMISSIONS, REQUEST_CODE);

        // 恢复页面时立即从上次断点开始播放视频
        if (mCurrentPosition>0 && !mPlayer.isPlaying()) {
            mPlayer.seekTo(mCurrentPosition); // 找到指定位置
        }
        mPlayer.play(); // 播放器开始播放
    }

    @Override
    public void onPause() {
        super.onPause();
        // 暂停页面时保存当前的播放进度
        if (mPlayer.isPlaying()) { // 播放器正在播放
            // 获得播放器当前的播放位置
            mCurrentPosition = mPlayer.getCurrentPosition();
            mPlayer.pause(); // 播放器暂停播放
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.release(); // 释放播放器资源
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
     * 播放视频
     * @param uri 视频Uri，既可以是本地视频，也可以是网络视频
     */
    private void playVideo(Uri uri) {
        // 创建指定视频格式的工厂对象
        DataSource.Factory factory = new DefaultDataSource.Factory(this);
        // 创建指定地址的媒体对象
        MediaItem videoItem = new MediaItem.Builder().setUri(uri).build();
        // 基于工厂对象和媒体对象创建媒体来源
        ProgressiveMediaSource videoSource = new ProgressiveMediaSource.Factory(factory).createMediaSource(videoItem);
        // 设置播放器的媒体来源
        mPlayer.setMediaSource(videoSource);
        // 给播放器添加事件监听器
        mPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_BUFFERING) { // 视频正在缓冲
                    Log.d(TAG, "视频正在缓冲...");
                } else if (state == Player.STATE_READY) { // 视频准备就绪
                    Log.d(TAG, "视频准备就绪");
                } else if (state == Player.STATE_ENDED) { // 视频播放完毕
                    Log.d(TAG, "视频播放完毕");
                }
            }
        });
        // 播放器准备就绪
        mPlayer.prepare();
        // 播放器开始播放
        mPlayer.play();
    }

    // 播放带字幕的视频
    private void playVideoWithSubtitle(Uri videoUri, Uri subtitleUri) {
        Log.d(TAG, "getLanguage="+ Locale.getDefault().getLanguage());
        // 创建HTTP在线视频的工厂对象
        DataSource.Factory factory = new DefaultDataSource.Factory(this);
        // 创建指定地址的媒体对象
        MediaItem videoItem = new MediaItem.Builder().setUri(videoUri).build();
        // 基于工厂对象和媒体对象创建媒体来源
        MediaSource videoSource = new ProgressiveMediaSource.Factory(factory)
                .createMediaSource(videoItem);
        // 语言要填null，否则中文会乱码。selectionFlags要填Format.NO_VALUE，否则看不到字幕
        // 创建指定地址的字幕对象。ExoPlayer只支持srt字幕，不支持ass字幕
        MediaItem.SubtitleConfiguration subtitleItem = new MediaItem.SubtitleConfiguration.Builder(subtitleUri)
                .setMimeType(MimeTypes.APPLICATION_SUBRIP)
                .setLanguage(null)
                .setSelectionFlags(Format.NO_VALUE)
                .build();
        // 过时方法
//        MediaItem.Subtitle subtitleItem = new MediaItem.Subtitle(subtitleUri,
//                MimeTypes.APPLICATION_SUBRIP, null, Format.NO_VALUE);
        // 基于工厂对象和字幕对象创建字幕来源
        MediaSource subtitleSource = new SingleSampleMediaSource.Factory(factory)
                .createMediaSource(subtitleItem, C.TIME_UNSET);
        // 合并媒体来源与字幕来源
        MergingMediaSource mergingSource = new MergingMediaSource(videoSource, subtitleSource);
        mPlayer.setMediaSource(mergingSource); // 设置播放器的媒体来源
        mPlayer.prepare(); // 播放器准备就绪
        mPlayer.play(); // 播放器开始播放
    }

    private String[] welcomeArray = {"首届（2018年）", "第二届（2019年）", "第三届（2020年）", "第四届（2021年）"};
    private String[] urlArray = {
            "https://ptgl.fujian.gov.cn:8088/masvod/public/2018/04/17/20180417_162d3639356_r38_1200k.mp4",
            "https://ptgl.fujian.gov.cn:8088/masvod/public/2019/04/15/20190415_16a1ef11c24_r38_1200k.mp4",
            "https://ptgl.fujian.gov.cn:8088/masvod/public/2020/09/26/20200926_174c8f9e4b6_r38_1200k.mp4",
            "https://ptgl.fujian.gov.cn:8088/masvod/public/2021/03/19/20210319_178498bcae9_r38.mp4",
    };

    /**
     * 初始化迎宾曲下拉框
     */
    private void initWelcomeSpinner() {
        ArrayAdapter<String> welcomeAdapter = new ArrayAdapter<>(this,
                R.layout.item_select, welcomeArray);
        Spinner sp_welcome = findViewById(R.id.sp_welcome);
        sp_welcome.setPrompt("请选择要播放的迎宾曲");
        sp_welcome.setAdapter(welcomeAdapter);
        sp_welcome.setOnItemSelectedListener(new WelcomeSelectedListener());
        sp_welcome.setSelection(3);
    }

    class WelcomeSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            playVideo(Uri.parse(urlArray[arg2])); // 播放视频
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
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