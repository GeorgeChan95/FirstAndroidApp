package com.george.chapter10.util;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class NotifyUtil {
    private final static String TAG = "GeorgeTag";

    /**
     * 创建通知渠道， Android 8.0开始必须给每个通知分配对应的渠道
     * @param ctx 上下文
     * @param channelId 消息通道编号
     * @param channelName 消息通道名称
     * @param importance 消息通道的重要性级别
     */
    @TargetApi(Build.VERSION_CODES.O)
    public static void createNotifyChannel(Context ctx, String channelId, String channelName, int importance) {
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        // 已经存在指定编号的通知渠道
        if (notificationManager.getNotificationChannel(channelId) == null) {
            // 创建指定编号、指定名称、指定级别的通知渠道
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            // 设置推送通知之时的铃声。null表示静音推送
            channel.setSound(null, null);
            // 通知渠道是否让呼吸灯闪烁
            channel.enableLights(true);
            // 通知渠道是否让手机震动
            channel.enableVibration(true);
            // 通知渠道是否在应用图标的右上角展示小红点
            channel.setShowBadge(true);
            // VISIBILITY_PUBLIC显示所有通知信息，VISIBILITY_PRIVATE只显示通知标题不显示通知内容，VISIBILITY_SECRET不显示任何通知信息
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            // 设置通知渠道的重要性级别
            channel.setImportance(importance);
            // 创建指定的通知渠道
            notificationManager.createNotificationChannel(channel);
        }
    }
}
