package com.george.chapter14;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.george.chapter14.listener.OnStopListener;
import com.george.chapter14.utils.PermissionUtil;
import com.george.chapter14.utils.Utils;
import com.george.chapter14.widget.CameraXView;

@SuppressLint("NewApi")
public class CameraxPhotoActivity extends AppCompatActivity {
    private final static String TAG = "GeorgeTag";

    public String[] PERMISSIONS = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    public final int REQUEST_CODE = 2;
    // 增强相机视图对象
    private CameraXView cxv_preview;
    // 黑屏专场视图对象
    private View v_black;
    // 拍摄按钮视图对象
    private ImageView iv_photo;
    // 声明一个处理器对象
    private final Handler mHandler = new Handler(Looper.myLooper());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerax_photo);
        // 初始化视图
        initView();
        // 初始化相机
        initCamera();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        cxv_preview = findViewById(R.id.cxv_preview);
        v_black = findViewById(R.id.v_black);
        iv_photo = findViewById(R.id.iv_photo);
        // 拍摄相片
        iv_photo.setOnClickListener(v -> {
            // 处理拍照动作
            dealPhoto();
        });
        // 切换摄像头
        findViewById(R.id.iv_switch).setOnClickListener(v -> cxv_preview.switchCamera());

        // 查看系统相册
        findViewById(R.id.btn_album).setOnClickListener(v -> {
            // 下面跳到系统相册界面
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            startActivity(intent);
        });

        // 查看当前拍摄的图片
        findViewById(R.id.btn_view).setOnClickListener(v -> {
            String photoPath = cxv_preview.getPhotoPath();
            if (TextUtils.isEmpty(photoPath)) {
                Toast.makeText(this, "当前未拍照, 请先拍一张照片", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, PhotoDetailActivity.class);
            intent.putExtra("photo_path", cxv_preview.getPhotoPath());
            startActivity(intent);
        });

        int adjustHeight = Utils.getScreenWidth(this) * 16 / 9;
        Log.d(TAG, "onResume getScreenWidth="+Utils.getScreenWidth(this)+", adjustHeight="+adjustHeight);
        ViewGroup.LayoutParams params = (RelativeLayout.LayoutParams) cxv_preview.getLayoutParams();
        params.height = adjustHeight;
        cxv_preview.setLayoutParams(params);
    }

    /**
     * 初始化相机
     */
    private void initCamera() {
        cxv_preview.openCamera(this, CameraXView.MODE_PHOTO, result -> {
            runOnUiThread(() -> {
                iv_photo.setEnabled(true);
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 打开页面，立即校验权限
        PermissionUtil.checkPermission(this, PERMISSIONS, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (PermissionUtil.checkGrant(grantResults)) {
                    Log.d(TAG, "CameraxPhotoActivity 权限获取成功");
                } else {
                    Toast.makeText(this, "权限获取异常", Toast.LENGTH_SHORT).show();
                    // 跳转到系统设置页面修改应用权限
                    jumpToSettings();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 处理点击拍摄动作
     */
    private void dealPhoto() {
        // 在当前拍摄动作完成之前，禁用拍摄按钮
        iv_photo.setEnabled(false);
        // 让拍摄区域黑一下，这样有拍摄的转场效果
        v_black.setVisibility(View.VISIBLE);
        // 拍摄照片
        cxv_preview.takePicture();
        // 屏幕 由黑->亮的效果
        mHandler.postDelayed(() -> v_black.setVisibility(View.GONE), 500);
    }

    /**
     * 跳转到 => 应用的系统设置页面
     */
    private void jumpToSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cxv_preview.closeCamera(); // 关闭相机
    }
}