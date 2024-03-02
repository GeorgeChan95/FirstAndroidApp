package com.george.chapter07_client.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.george.chapter07_client.ProviderMmsActivity;
import com.george.chapter07_client.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
    private static String TAG = "GeorgeTag";
    /**
     * 保存文本内容到本地
     * @param path 文件路径
     * @param txt 文本内容
     */
    public static void saveText(String path, String txt) {
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(path));
            bufferedWriter.write(txt);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从指定路径读取文本内容
     * @param path
     * @return
     */
    public static String openText(String path) {
        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    /**
     * 保存位图到指定路径
     * @param path 图片保存路径
     * @param bitmap 位图数据
     */
    public static void saveImage(String path, Bitmap bitmap) {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            boolean compress = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            if (compress) {
                Log.d(TAG, "图片保存成功，路径：" + path);
            }
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取图片
     * @param path
     * @return
     */
    public static Bitmap openImage(String path) {
        Bitmap bitmap = null;
        try (FileInputStream fis = new FileInputStream(path)) {
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 校验文件是否属于 file_paths.xml配置允许的路径下
     * @param ctx 上下文
     * @param path 文件路径
     */
    public static boolean checkFileUri(Activity ctx, String path) {
        File file = new File(path);
        if (!file.exists() || !file.isFile() || file.length() <= 0) {
            return false;
        }

        // 如果是安卓7.0及以上，文件访问需要使用FileProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                // 使用FileProvider获取文件访问的Uri，如果没有出现异常，表示文件符合要求
                Uri uri = FileProvider.getUriForFile(ctx, ctx.getString(R.string.file_provider), file);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
