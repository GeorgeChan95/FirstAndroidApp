package com.george.chapter06;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class FilePathActivity extends AppCompatActivity {
    private String TAG = "GeorgeTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_path);
        // 选择开启读写权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        // 开启所有文件访问权限
//        if (Build.VERSION.SDK_INT >= 30){
//            if (!Environment.isExternalStorageManager()){
//                Intent getpermission = new Intent();
//                getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                startActivity(getpermission);
//            }
//        }


        TextView tv_path = findViewById(R.id.tv_path);

        // 获取系统的公共存储路径
        String publicPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

        // 获取当前APP的私有存储路径
        String privatePath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();

        boolean isLegacy = true;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            Log.d(TAG, "SDK_INT: " + Build.VERSION.SDK_INT);
            // Android10的存储空间默认采取分区方式，此处判断是传统方式还是分区方式
            isLegacy = Environment.isExternalStorageLegacy();
        }

        String desc = "系统的公共存储路径位于" + publicPath +
                "\n当前App的私有存储路径位于" + privatePath +
                "\nAndroid7.0之后默认禁止访问公共存储目录" +
                "\n当前App的存储空间采取" + (isLegacy?"传统方式":"分区方式");

        tv_path.setText(desc);



    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}