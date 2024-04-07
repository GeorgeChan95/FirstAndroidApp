package com.george.chapter10.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.george.chapter10.NoscrollListActivity;
import com.george.chapter10.R;


public class MusicService extends Service {

    // 创建一个粘合剂对象
    private final IBinder mBinder = new LocalBinder();
    // 声明一个处理器对象
    private Handler mHandler = new Handler(Looper.myLooper());
    // 是否正在播放
    private boolean isPlaying = true;
    // 歌曲名称
    private String mSong;
    // 播放进度
    private int mProcess = 0;

    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    private Runnable playRun = new Runnable() {
        @Override
        public void run() {
            if (isPlaying) {
                if (mProcess < 100) {
                    mProcess += 2;
                    mHandler.postDelayed(this, 3000);
                } else {
                    mProcess = 100;
                }
            }
            sendNotify(MusicService.this, mSong, isPlaying, mProcess);
        }
    };

    /**
     * 发送前台通知
     * @param ctx
     * @param mSong
     * @param isPlaying
     * @param mProcess
     */
    private void sendNotify(Context ctx, String mSong, boolean isPlaying, int mProcess) {
        String message = String.format("歌曲%s", isPlaying ? "正在播放" : "暂停播放");
        // 点击通知，跳转的意图
        Intent intent = new Intent(ctx, NoscrollListActivity.class);
        // 创建一个用于页面跳转的延迟意图
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, R.string.app_name, intent, PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder builder = new Notification.Builder(ctx);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8.0 要求给每个通知绑定一个通道
            builder = new Notification.Builder(ctx, getString(R.string.app_name));
        }

        Notification notification = builder
                // 设置内容的点击意图
                .setContentIntent(pendingIntent)
                // 设置应用名称左边的小图标
                .setSmallIcon(R.drawable.tt_s)
                // 设置通知栏右边的大图标
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.tt))
                // 设置通知栏里面的标题文本
                .setContentTitle(mSong)
                // 设置通知栏里面的内容文本
                .setContentText(message)
                // 设置当前进度
                .setProgress(100, mProcess, false).build();
        // 把服务推送到前台的通知栏
        startForeground(2, notification);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 获取是否正在播放歌曲的标记
        isPlaying = intent.getBooleanExtra("is_playing", true);
        // 获取歌曲名称
        mSong = intent.getStringExtra("song");
        mHandler.postDelayed(playRun, 200);
        return START_STICKY;
    }


}
