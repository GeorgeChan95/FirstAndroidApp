package com.george.chapter06;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.george.chapter06.util.FileUtil;
import com.george.chapter06.util.ToastUtil;

import java.io.File;

public class ImageReadWriteActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_content;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_read_write);
        iv_content = findViewById(R.id.iv_content);

        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.btn_read).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                String fileName = System.currentTimeMillis() + ".jpeg";
                // 公共存储的私有下载目录下
                path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + fileName;
                // 从指定的资源文件中获取位图数据
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.oppo);
                FileUtil.saveImage(path, bitmap);
                ToastUtil.show(this, "图片保存成功");
                break;
            case R.id.btn_read:
//                    // 从指定的输入流中获取位图数据。
//                bitmap = FileUtil.openImage(path);

                // 从指定路径的文件中获取位图数据。注意从Android 10开始，该方法只适用于私有目录下的图片，不适用公共空间下的图片。
//                bitmap = BitmapFactory.decodeFile(path);
//                iv_content.setImageBitmap(bitmap);

                // 直接调用setImageURI方法，设置图像视图的路径对象
                iv_content.setImageURI(Uri.parse(path));
                break;
        }
    }
}