package com.george.chapter14;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        VideoView vv_content = findViewById(R.id.vv_content);
        // 拿到参数：视频保存路径
        String video_path = getIntent().getStringExtra("video_path");
        // 设置视频视图的路径
        vv_content.setVideoURI(Uri.parse("file://" + video_path));
        // 创建媒体控制条
        MediaController mc = new MediaController(this);
        // 给视频视图设置媒体控制条
        vv_content.setMediaController(mc);
        // 给媒体控制条设置关联的视频视图
        mc.setMediaPlayer(vv_content);
        // 开始播放视频
        vv_content.start();
    }
}