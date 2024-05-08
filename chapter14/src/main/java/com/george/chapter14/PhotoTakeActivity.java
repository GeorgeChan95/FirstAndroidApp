package com.george.chapter14;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.george.chapter14.utils.BitmapUtil;
import com.george.chapter14.utils.DateUtil;
import com.george.chapter14.utils.PermissionUtil;

public class PhotoTakeActivity extends AppCompatActivity {
    private final static String TAG = "GeorgeTag";

    private ImageView iv_photo; // 声明图像视图对象
    private Uri mImageUri; // 图片的路径对象
    // 声明一个活动结果的启动对象
    private ActivityResultLauncher<Void> launcherThumbnail;
    // 声明一个活动结果启动器对象
    private ActivityResultLauncher<Uri> launcherOriginal;

    public String[] PERMISSIONS = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_take);

        iv_photo = findViewById(R.id.iv_photo);
        // 注册一个善后工作的活动结果启动器，准备打开拍照界面（返回缩略图）
        launcherThumbnail = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bitmap -> iv_photo.setImageBitmap(bitmap));
        // 点击按钮，触发活动定义好的活动启动器
        findViewById(R.id.btn_thumbnail).setOnClickListener(v -> launcherThumbnail.launch(null));

        launcherOriginal = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
            if (result) {
                // 获取自动缩放后的位图对象
                Bitmap zoomImage = BitmapUtil.getAutoZoomImage(PhotoTakeActivity.this, mImageUri);

                // 获取拍摄图片的真实路径
                String filePath = "";
                String scheme = mImageUri.getScheme();
                if (mImageUri.getScheme().equals("file")) {
                    filePath = mImageUri.getPath();
                } else if (mImageUri.getScheme().equals("content")) {
                    Cursor cursor = getContentResolver().query(mImageUri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        filePath = cursor.getString(index);
                        cursor.close();
                    }
                }

                Log.d(TAG, "拍摄照片真实路径 filePath: " + filePath);
                // 设置位图对象显示到页面
                iv_photo.setImageBitmap(zoomImage);
            }
        });
        // 点击拍照按钮，执行拍照，并获取原始图片的Uri
        findViewById(R.id.btn_original).setOnClickListener(v -> takeOriginalPhoto());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 打开页面，立即校验权限
        PermissionUtil.checkPermission(this, PERMISSIONS, REQUEST_CODE);
    }

    /**
     * 拍照，并获取原始图片
     */
    private void takeOriginalPhoto() {
        // Android10开始必须由系统自动分配路径，同时该方式也能自动刷新相册
        ContentValues values = new ContentValues();
        // 指定图片文件名称
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "photo_"+ DateUtil.getNowDateTime());
        // 指定类型为图像
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        // 通过内容解析器插入一条外部内容的路径信息
        mImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        launcherOriginal.launch(mImageUri);
    }
}