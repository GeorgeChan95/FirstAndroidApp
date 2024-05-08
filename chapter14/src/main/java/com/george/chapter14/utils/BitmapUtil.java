package com.george.chapter14.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
}
