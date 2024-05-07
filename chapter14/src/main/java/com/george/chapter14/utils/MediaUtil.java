package com.george.chapter14.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.george.chapter14.MediaRecorderActivity;

import java.io.File;

public class MediaUtil {
    private final static String TAG = "MediaUtil";
    /**
     * 毫秒时间格式化
     * @param milliseconds
     * @return
     */
    public static String formatDuration(int milliseconds) {
        int seconds = milliseconds / 1000;
        int hour = seconds / 3600;
        int minute = seconds / 60;
        int second = seconds % 60;
        String str;
        if (hour > 0) {
            str = String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            str = String.format("%02d:%02d", minute, second);
        }
        return str;
    }

    /**
     * 获取录制文件路径
     * @param context 上下文
     * @param dir_name 文件目录名
     * @param extend_name 文件扩展名
     * @return
     */
    public static String getRecordFilePath(Context context, String dir_name, String extend_name) {
        String path = "";
        File recordDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + dir_name + "/");
        if (!recordDir.exists()) {
            recordDir.mkdir();
        }

        try {
            File recordFile = File.createTempFile(DateUtil.getNowDateTime(), extend_name, recordDir);
            path = recordFile.getAbsolutePath();
            String log = String.format("执行getRecordFilePath， dir_name: %s , extend_name: %s , path: %s", dir_name, extend_name, path);
            Log.d(TAG, log);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }
}
