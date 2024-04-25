package com.george.chapter13;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkhttpDownloadActivity extends AppCompatActivity {

    private TextView tv_result; // 声明一个文本视图对象
    private TextView tv_progress; // 声明一个文本视图对象
    private ImageView iv_result; // 声明一个图像视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okhttp_download);
        tv_result = findViewById(R.id.tv_result);
        tv_progress = findViewById(R.id.tv_progress);
        iv_result = findViewById(R.id.iv_result);
        findViewById(R.id.btn_download_image).setOnClickListener(v -> downloadImage());
        findViewById(R.id.btn_download_file).setOnClickListener(v -> downloadFile());
    }

    /**
     * 图片下载
     */
    private void downloadImage() {
        tv_progress.setVisibility(View.GONE);
        iv_result.setVisibility(View.VISIBLE);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.6.209:8008/down/image")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // 回到主线程操纵界面
                runOnUiThread(() -> tv_result.setText("下载网络图片报错："+e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                InputStream is = response.body().byteStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                String contentType = response.body().contentType().toString();
                long length = response.body().contentLength();
                String desc = String.format("文件类型：%s, 文件大小：%d", contentType, length);
                runOnUiThread(() -> {
                    tv_result.setText("下载图片返回：" + desc);
                    iv_result.setImageBitmap(bitmap);
                });
            }
        });
    }

    /**
     * 文件下载
     */
    private void downloadFile() {
        tv_progress.setVisibility(View.VISIBLE);
        iv_result.setVisibility(View.GONE);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.6.209:8008/down/file")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // 回到主线程操纵界面
                runOnUiThread(() -> tv_result.setText("下载网络文件报错："+e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                String contentType = response.body().contentType().toString();
                long length = response.body().contentLength();
                long mLen = length/1024/1024;
                String desc = String.format("文件类型：%s, 文件大小：%d M", contentType, mLen);
                // 回到主线程操纵界面
                runOnUiThread(() -> tv_result.setText("下载网络文件返回："+desc));
                String path = String.format("%s/%s.zip", getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(), "20240425110600");

                // 下面从返回的输入流中读取字节数据并保存为本地文件
                try (InputStream is = response.body().byteStream();
                     FileOutputStream fos = new FileOutputStream(path)) {
                    byte[] buf = new byte[100 * 1024];
                    int sum = 0, len = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / length * 100);
                        String detail = String.format("文件保存在：%s, 已下载%d%%", path, progress);
                        runOnUiThread(() -> tv_progress.setText(detail));
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}