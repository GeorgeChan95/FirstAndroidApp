package com.george.chapter17;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.george.chapter17.util.IPv4Util;
import com.george.chapter17.util.PermissionUtil;

public class WifiInfoActivity extends AppCompatActivity {
    private String TAG = "GeorgeTag";

    private TextView tv_info;
    private Handler mHandler = new Handler(Looper.myLooper()); // 声明一个处理器对象
    private String[] mWifiStateArray = {"正在断开", "已断开", "正在连接", "已连接", "未知"};

    public String[] PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    public final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_info);

        tv_info = findViewById(R.id.tv_info);
        // 延迟50毫秒后启动网络刷新任务
        mHandler.postDelayed(mRefresh, 100);
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
                    Log.d(TAG, "权限获取成功");
                } else {
                    Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show();
                    jumpToSettings();
                }
                break;
            default:
                break;
        }
    }

    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            // 获取可用的网络信息
            getAvailableNet();
            // 延迟1秒后再次启动网络刷新任务
            mHandler.postDelayed(this, 1000);
        }
    };

    /**
     * 获取可用的网络信息
     */
    @SuppressLint("MissingPermission")
    public void getAvailableNet() {
        String desc = "";
        // 从系统服务中获取电话管理器
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // 从系统服务中获取连接管理器
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // 通过连接管理器获得可用的网络信息
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.getState() == NetworkInfo.State.CONNECTED) { // 有网络连接
            if (info.getType() == ConnectivityManager.TYPE_WIFI) { // 如果当前是WIFI连接
                // 从系统服务中获取无线网络管理器
                WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                int state = wm.getWifiState(); // 无线网络连接状态
                // 网络连接信息
                WifiInfo wifiInfo = wm.getConnectionInfo();
                // 获取无线网络名称
                String ssid = wifiInfo.getSSID();
                if (TextUtils.isEmpty(ssid) || ssid.contains("unknown")) {
                    desc = "\n当前联网的网络类型是WiFi，但未成功连接已知的WiFi信号";
                } else {
                    desc = String.format("当前联网的网络类型是WiFi，状态是%s。\nWiFi名称是：%s\n路由器MAC是：%s\nWiFi信号强度是：%d\n连接速率是：%s\n手机的IP地址是：%s\n手机的MAC地址是：%s\n网络编号是：%s\n",
                            mWifiStateArray[state], ssid, wifiInfo.getBSSID(),
                            wifiInfo.getRssi(), wifiInfo.getLinkSpeed(),
                            IPv4Util.intToIp(wifiInfo.getIpAddress()),
                            wifiInfo.getMacAddress(), wifiInfo.getNetworkId());
                }
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) { // 如果当前是移动网络连接

            } else {
                desc = String.format("\n当前联网的网络类型是%d", info.getType());
            }
        } else { // 无网络连接
            desc = "\n当前无上网连接";
        }
        tv_info.setText(desc);
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