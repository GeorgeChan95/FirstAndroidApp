package com.george.chapter14.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class BitmapUtil {
    private final static String TAG = "BitmapUtil";

    /**
     * 获取按比例缩小后的位图对象
     * @param ctx
     * @param uri
     * @return
     */
    public static Bitmap getAutoZoomImage(Context ctx, Uri uri) {
        Log.d(TAG, "getAutoZoomImage uri: " + uri);
        Bitmap zoomBitmap = null;
        // 打开指定Uri获得输入流对象
        try (InputStream is = ctx.getContentResolver().openInputStream(uri)) {
            // 从输入流中获得原始位图对象
            Bitmap originBitmap = BitmapFactory.decodeStream(is);
            // 获取缩放比例
            int radio = originBitmap.getWidth()/2000 + 1;
            // 获得缩放比例后的位图对象
            zoomBitmap = BitmapUtil.getScaleBitmap(originBitmap, 1.0/radio);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zoomBitmap;
    }

    /**
     * 获取按比例缩小后的位图对象
     * @param bitmap 原始位图对象
     * @param scaleRatio 缩放比例
     * @return
     */
    private static Bitmap getScaleBitmap(Bitmap bitmap, double scaleRatio) {
        int new_width = (int) (bitmap.getWidth() * scaleRatio);
        int new_height = (int) (bitmap.getHeight() * scaleRatio);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, new_width, new_height, false);
        return scaledBitmap;
    }

    /**
     * 通知相册，有了新的照片
     * @param ctx 上下文对象
     * @param filePath 新照片的路径
     */
    public static void notifyPhotoAlbum(Context ctx, String filePath) {
        try {
            String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
            // 将指定的图像文件添加到相册中。
            // 第一个参数是内容解析器，用于访问相册数据库；第二个参数是文件路径；第三个参数是文件名；第四个参数是描述，可以为空。
            MediaStore.Images.Media.insertImage(ctx.getContentResolver(), filePath, fileName, null);
            // 将文件路径转换成Uri格式
            Uri uri = Uri.parse("file://" + filePath);
            // 创建了一个广播意图，用于通知系统扫描指定的文件，使其出现在媒体库中
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
            // 发送了广播，通知系统扫描指定的文件
            ctx.sendBroadcast(intent);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "通知相册有新照片操作异常，异常信息：" + e.getMessage());
        }
    }

    /**
     * 获得自动缩小后的位图对象
     * @param origin
     * @return
     */
    public static Bitmap getAutoZoomImage(Bitmap origin) {
        int ratio = origin.getWidth()/2000+1;
        // 获得比例缩放之后的位图对象
        Bitmap zoomBitmap = getScaleBitmap(origin, 1.0/ratio);
        return zoomBitmap;
    }
}
