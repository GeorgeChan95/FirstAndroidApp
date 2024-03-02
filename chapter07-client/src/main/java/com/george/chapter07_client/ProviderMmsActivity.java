package com.george.chapter07_client;

import static android.content.Intent.EXTRA_STREAM;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.george.chapter07_client.entity.ImageInfo;
import com.george.chapter07_client.util.FileUtil;
import com.george.chapter07_client.util.LayoutUtils;
import com.george.chapter07_client.util.PermissionUtil;
import com.george.chapter07_client.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用FileProvider发送彩信
 */
public class ProviderMmsActivity extends AppCompatActivity {
    private String TAG = "GeorgeTag";

    // 需要校验的权限集合
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    // 权限校验唯一编码
    private static final int PERMISSION_REQUEST_CODE = 1;

    private GridLayout gl_appendix;
    private EditText et_phone;
    private EditText et_title;
    private EditText et_message;
    List<ImageInfo> imageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_mms);
        et_phone = findViewById(R.id.et_phone);
        et_title = findViewById(R.id.et_title);
        et_message = findViewById(R.id.et_message);
        gl_appendix = findViewById(R.id.gl_appendix);

        //手动让MediaStore扫描入库
        MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                Log.d(TAG, "扫描到文件path: +" + path + ",  uri: " + uri.toString());
            }
        });

        // 检查当前页面是否有权限
        boolean res = PermissionUtil.checkPermission(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        if (res) {
            // 加载图片数据集
            loadImageList();
            // 将图片显示到GridLayout中
            showImageGrid();
        }
    }

    /**
     * 权限点选后的回调方法
     * @param requestCode 返回的校验码，对应检查权限时定义的编码
     * @param permissions 校验的权限集合
     * @param grantResults 每个权限校验的结果集合，与 @permissions 一一对应
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && PermissionUtil.checkGrant(grantResults)) {
            // 加载图片数据集
            loadImageList();
            // 将图片显示到GridLayout中
            showImageGrid();
        }
    }

    /**
     * 将查询的图片，填充到网格区域中
     */
    private void showImageGrid() {
        if (imageList.isEmpty()) return;

        for (ImageInfo image : imageList) {
            ImageView imageView = new ImageView(this);
            // 设置图片路径
            imageView.setImageBitmap(BitmapFactory.decodeFile(image.path));
            // 设置图片缩放类型
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            // 设置图像视图的布局参数
            int px = LayoutUtils.dip2px(this, 110);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(px, px);
            imageView.setLayoutParams(params);
            // 设置视图的内部间距
            int paddingPx = LayoutUtils.dip2px(this, 5);
            imageView.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);

            // 给图片添加点击事件
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 发送彩信方法
                    sendMms(et_phone.getText().toString(), et_title.getText().toString(), et_message.getText().toString(), image.path);
                }
            });
            // 将图像视图添加到网格视图中
            gl_appendix.addView(imageView);
        }
    }

    /**
     * 发送彩信
     * @param phone 手机号
     * @param title 主题
     * @param message 消息内容
     * @param path 图片附件
     */
    private void sendMms(String phone, String title, String message, String path) {
        // 根据指定的文件路径创建一个Uri对象
        Uri uri = Uri.parse(path);
        // 如果安卓版本在7.0及以上，需要受用FileProvider获取一个文件的Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this, getString(R.string.file_provider), new File(path));
            Log.d(TAG, "开始发送彩信，文件Uri：" + uri.toString());
        }

        // 创建一个发送动作的意图
        Intent intent = new Intent(Intent.ACTION_SEND);
        // 另外开启新页面
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Intent 的接受者将被准许读取Intent 携带的URI数据
        intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
        // 彩信发送的目标号码
        intent.putExtra("address", phone);
        // 彩信的标题
        intent.putExtra("subject", title);
        // 彩信的内容
        intent.putExtra("sms_body", message);
        // 彩信图片附件
        intent.putExtra(EXTRA_STREAM, uri);
        // 附件类型
        intent.setType("image/*");
        // 因为未指定要打开哪个页面，所以系统会在底部弹出选择窗口
        startActivity(intent);
        ToastUtil.show(this, "请在弹窗中选择短信或者信息应用");
    }

    /**
     * 加载图片数据集
     */
    @SuppressLint("Range")
    private void loadImageList() {
        // 定义查询文件的列
        String[] column  = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.SIZE
        };

        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, "_size < 1048576", null, "_size DESC");
        // 只取6张图片
        int count = 0;
        if (cursor != null) {
            while (cursor.moveToNext() && count < 6) {
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                imageInfo.name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
                imageInfo.size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                imageInfo.path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                // 判断文件是否符合要求：文件来自于 file_paths.xml配置允许的路径下
                boolean res = FileUtil.checkFileUri(this, imageInfo.path);
                if (res) {
                    count++;
                    imageList.add(imageInfo);
                }
                Log.d(TAG, "获取到图片：" + imageInfo.toString());
            }
        }
        cursor.close();
    }
}