package com.george.chapter07_client;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.george.chapter07_client.util.PermissionUtil;
import com.george.chapter07_client.util.ToastUtil;

import java.io.File;

public class ProviderApkActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "GeorgeTag";

    // 需要校验的权限集合
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    // 权限校验唯一编码
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_apk);

        findViewById(R.id.btn_install).setOnClickListener(this);
    }

    /**
     * 设置权限后的回调方法
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 如果设置权限后，回调检验权限满足了，执行Apk安装
        if (requestCode == PERMISSION_REQUEST_CODE && PermissionUtil.checkGrant(grantResults)) {
            installApk();
        }
    }

    @Override
    public void onClick(View v) {
        // 如果是安卓11.0及以上，使用FileProvider自动打开安装包，需要MANAGE_EXTERNAL_STORAGE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.d(TAG, "当前使用的是安卓11.0及以上版本");
            // 检查应用是否有 MANAGE_EXTERNAL_STORAGE 权限
            checkAndInstall();
        } else {
            // 检查是否有文件读写权限
            if (PermissionUtil.checkPermission(this, PERMISSIONS, PERMISSION_REQUEST_CODE)) {
                // 安装APK
                installApk();
            }
        }
    }

    /**
     * 检查在安卓11.0及以上版本的系统上，应用是否具有 MANAGE_EXTERNAL_STORAGE 权限
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void checkAndInstall() {
        // 检查应用是否具有  MANAGE_EXTERNAL_STORAGE 权限, 没有则跳转到设置页面
        if (!Environment.isExternalStorageManager()) {
            // 系统所有文件管理权限的设置
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            // 开启新的页面
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.fromParts("package", getPackageName(), null));
            startActivity(intent);
        } else {
            // 如果有权限，直接安装
            installApk();
        }
    }


    /**
     * 执行安装Apk
     */
    private void installApk() {
        // 获取apk所在路径
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/base.apk";
        Log.d(TAG, "获取到APK安装包路径：" + path);

        // 获取系统包管理器
        PackageManager pm = getPackageManager();

        // 获取apk压缩包的信息
        PackageInfo pi = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if (pi == null) {
            Log.d(TAG, "安装APK操作失败，安装包已损坏，path：" + path);
            ToastUtil.show(this, "安装包已损坏");
            return;
        }

        Uri uri = Uri.parse(path);
        // 兼容Android7.0，把访问文件的Uri方式改为FileProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this, getString(R.string.file_provider), new File(path));
        }
        Log.d(TAG, "将要安装的APK 的 Uri为：" + uri);

        // 创建一个浏览动作的意图
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 另外打开一个新的页面
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Intent 的接受者将被准许读取Intent 携带的URI数据
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // 设置Uri的数据类型为APK文件
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        // 启动系统自带的应用安装程序
        startActivity(intent);
    }
}