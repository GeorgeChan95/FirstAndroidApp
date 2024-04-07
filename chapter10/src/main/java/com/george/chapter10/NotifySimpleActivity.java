package com.george.chapter10;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.george.chapter10.util.ToastUtil;
import com.george.chapter10.util.ViewUtil;

public class NotifySimpleActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_title;
    private EditText et_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_simple);

        et_title = findViewById(R.id.et_title);
        et_message = findViewById(R.id.et_message);
        findViewById(R.id.btn_send_simple).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send_simple) {
            // 隐藏输入法软键盘
            ViewUtil.hideOneInputMethod(this, et_message);
            ViewUtil.hideOneInputMethod(this, et_title);
            if (TextUtils.isEmpty(et_title.getText())) {
                ToastUtil.show(this, "请填写消息标题");
            }
            if (TextUtils.isEmpty(et_message.getText())) {
                ToastUtil.show(this, "请填写消息内容");
            }

            String title = et_title.getText().toString();
            String message = et_message.getText().toString();
            // 发送简单的通知消息
            sendSimpleNotify(title, message);
        }
    }

    /**
     * 发送简单的通知消息
     * @param title
     * @param message
     */
    private void sendSimpleNotify(String title, String message) {
        // 发送消息之前要先创建通知渠道，创建代码见MainApplication.java

        // 创建一个用于点击消息通知后，跳转页面的意图
        Intent clickIntent = new Intent(this, NoscrollListActivity.class);
        // 创建一个用于页面跳转的延迟意图
        PendingIntent pendingIntent = PendingIntent.getActivity(this, R.string.app_name, clickIntent, PendingIntent.FLAG_IMMUTABLE);
        // 创建一个通知消息的构造器
        Notification.Builder builder = new Notification.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8.0 及以上，必须给每个消息通知分配对应的渠道
            builder = new Notification.Builder(this, getString(R.string.app_name));
        }
        // 设置消息通知的延迟意图
        builder.setContentIntent(pendingIntent)
                // 设置点击消息后自动清除通知
                .setAutoCancel(true)
                // 设置应用名称左边的小图标（必须有）
                .setSmallIcon(R.mipmap.ic_launcher)
                // 设置通知栏的附加文本
                .setSubText("消息副本")
                // 设置通知栏右边的大图标
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_app))
                // 设置通知栏的标题文本
                .setContentTitle(title)
                // 设置通知栏的内容文本
                .setContentText(message);
        // 构建通知
        Notification notification = builder.build();
        // 从系统服务中获取通知管理器
        NotificationManager notificationMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // 把指定消息推送到通知栏，然后通知栏就能看到一条消息
        notificationMgr.notify(R.string.app_name, notification);
    }
}