package com.george.chapter10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.george.chapter10.service.MusicService;
import com.george.chapter10.util.ToastUtil;

public class ForegroundServiceActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_song;
    private Button btn_send_service;
    private boolean is_playing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foreground_service);

        et_song = findViewById(R.id.et_song);
        btn_send_service = findViewById(R.id.btn_send_service);
        btn_send_service.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send_service) {
            if (TextUtils.isEmpty(et_song.getText())) {
                ToastUtil.show(this, "请输入歌曲名称");
                return;
            }
            Intent intent = new Intent(this, MusicService.class);
            intent.putExtra("is_playing", is_playing);
            intent.putExtra("song", et_song.getText().toString());
            btn_send_service.setText(is_playing ? "暂停播放":"继续播放");
            startService(intent);
            is_playing = !is_playing;
        }
    }
}