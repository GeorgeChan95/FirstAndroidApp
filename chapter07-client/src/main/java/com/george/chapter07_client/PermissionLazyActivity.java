package com.george.chapter07_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.george.chapter07_client.R;
import com.george.chapter07_client.util.PermissionUtil;
import com.george.chapter07_client.util.ToastUtil;

public class PermissionLazyActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "GeorgeTag";

    public String[] PERMISSIONS_CONTACTS = new String[] {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
    };

    public String[] PERMISSIONS_SMS = new String[] {
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS
    };

    public final int REQUEST_CODE_CONTACTS = 1;
    public final int REQUEST_CODE_SMS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_lazy);
        findViewById(R.id.btn_contact).setOnClickListener(this);
        findViewById(R.id.btn_sms).setOnClickListener(this);
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