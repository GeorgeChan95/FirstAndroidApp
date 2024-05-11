package com.george.chapter17;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.george.chapter17.adapter.ScanListAdapter;
import com.george.chapter17.util.PermissionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WifiScanActivity extends AppCompatActivity {
    private final static String TAG = "GeorgeTag";

    private TextView tv_result;
    // 显示wifi数据列表的列表视图
    private ListView lv_scan;
    // Wifi管理器对象
    private WifiManager mWifiManager;
    private WifiScanReceiver mWifiScanReceiver = new WifiScanReceiver();

    public String[] PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    public final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_scan);

        // 提示文本
        tv_result = findViewById(R.id.tv_result);
        // WIFI列表视图
        lv_scan = findViewById(R.id.lv_scan);
        // 点击按钮，开始扫描附近的WIFI
        findViewById(R.id.btn_scan).setOnClickListener(v -> mWifiManager.startScan());
        // 从系统服务中获取WIFI管理器
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 校验权限
        PermissionUtil.checkPermission(this, PERMISSIONS, REQUEST_CODE);

        IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        // 注册WIFI扫描的广播接收器
        registerReceiver(mWifiScanReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 注销WIFI扫描广播接收器
        unregisterReceiver(mWifiScanReceiver);
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

    /**
     * 定义一个扫描周边的WIFI的广播接收器
     */
    private class WifiScanReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取WIFI扫描结果
            List<ScanResult> scanList = mWifiManager.getScanResults();
            if (scanList != null) {
                // 查找符合80211标准的WiFi路由器集合
                Map<String, ScanResult> m80211mcMap = find80211mcResult(scanList);
                // 刷新页面
                runOnUiThread(() -> showScanResult(scanList, m80211mcMap));
            }
        }
    }

    /**
     * 查找符合80211标准的WiFi路由器集合
     */
    private Map<String, ScanResult> find80211mcResult(List<ScanResult> originList) {
        Map<String, ScanResult> resultMap = new HashMap<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Android6.0及以后支持室内WIFI定位
            for (ScanResult scanResult : originList) {
                if (scanResult.is80211mcResponder()) {
                    resultMap.put(scanResult.BSSID, scanResult);
                }
            }
        }
        return resultMap;
    }

    /**
     * 显示过滤后的WiFi扫描结果
     * @param list
     * @param map
     */
    private void showScanResult(List<ScanResult> list, Map<String, ScanResult> map) {
        tv_result.setText(String.format("找到%d个WiFi热点，其中有%d个支持RTT。",
                list.size(), map.size()));
        // 给列表视图设置适配器
        lv_scan.setAdapter(new ScanListAdapter(this, list, map));
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