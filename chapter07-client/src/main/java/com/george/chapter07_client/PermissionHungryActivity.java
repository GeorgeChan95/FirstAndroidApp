package com.george.chapter07_client;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.george.chapter07_client.util.PermissionUtil;
import com.george.chapter07_client.util.ToastUtil;

/**
 * 饿汉式权限加载：打开应用即加载所有权限
 */
public class PermissionHungryActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "GeorgeTag";

    public String[] PERMISSIONS_ALL = new String[] {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS
    };
    public String[] PERMISSIONS_CONTACTS = new String[] {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
    };

    public String[] PERMISSIONS_SMS = new String[] {
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS
    };

    public final int REQUEST_CODE_ALL = 1;
    public final int REQUEST_CODE_CONTACTS = 2;
    public final int REQUEST_CODE_SMS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_lazy);
        findViewById(R.id.btn_contact).setOnClickListener(this);
        findViewById(R.id.btn_sms).setOnClickListener(this);
        // 打开应用立即校验权限
        PermissionUtil.checkPermission(this, PERMISSIONS_ALL, REQUEST_CODE_ALL);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_contact:
                PermissionUtil.checkPermission(this, PERMISSIONS_CONTACTS, REQUEST_CODE_CONTACTS);
                break;
            case R.id.btn_sms:
                PermissionUtil.checkPermission(this, PERMISSIONS_SMS, REQUEST_CODE_SMS);
                break;
        }
    }

    /**
     * 用于在权限选择弹框，点击时的回调
     * @param requestCode 自定义权限类型码，标识用户当前操作的是什么权限
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_ALL:
                if (PermissionUtil.checkGrant(grantResults)) {
                    Log.d(TAG, "所有权限获取成功");
                } else {
                    // 遍历每个权限的校验结果，看具体哪个权限没有获取成功
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            switch (permissions[i]) {
                                case Manifest.permission.READ_CONTACTS:
                                case Manifest.permission.WRITE_CONTACTS:
                                    Log.d(TAG, "通讯录权限获取失败");
                                    jumpToSettings();
                                    break;
                                case Manifest.permission.SEND_SMS:
                                case Manifest.permission.READ_SMS:
                                case Manifest.permission.RECEIVE_SMS:
                                    Log.d(TAG, "短信权限获取失败");
                                    jumpToSettings();
                                    break;
                            }
                        }

                    }
                }
                break;
            case REQUEST_CODE_CONTACTS:
                if (PermissionUtil.checkGrant(grantResults)) {
                    Log.d(TAG, "通讯录权限获取成功");
                } else {
                    ToastUtil.show(this, "获取通讯录读写权限失败");
                    jumpToSettings();
                }
                break;
            case REQUEST_CODE_SMS:
                if (PermissionUtil.checkGrant(grantResults)) {
                    Log.d(TAG, "收发短信权限获取成功");
                } else {
                    ToastUtil.show(this, "获取收发短信权限失败！");
                    jumpToSettings();
                }
                break;
        }
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
}
