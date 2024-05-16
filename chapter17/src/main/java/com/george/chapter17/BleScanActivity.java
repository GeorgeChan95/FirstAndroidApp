package com.george.chapter17;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.george.chapter17.adapter.BlueListAdapter;
import com.george.chapter17.entity.BlueDevice;
import com.george.chapter17.util.BluetoothUtil;
import com.george.chapter17.util.PermissionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressLint("MissingPermission")
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BleScanActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "GeorgeTag";

    private CheckBox ck_bluetooth; // 声明一个复选框对象
    private TextView tv_discovery; // 声明一个文本视图对象
    private ListView lv_bluetooth; // 声明一个用于展示蓝牙设备的列表视图对象
    private BlueListAdapter mListAdapter; // 声明一个蓝牙设备的列表适配器对象

    private Map<String, BlueDevice> mDeviceMap = new HashMap<>(); // 蓝牙设备映射
    private List<BlueDevice> mDeviceList = new ArrayList<>(); // 蓝牙设备列表
    private Handler mHandler = new Handler(Looper.myLooper()); // 声明一个处理器对象
    private BluetoothAdapter mBluetoothAdapter; // 声明一个蓝牙适配器对象
    private BluetoothDevice mRemoteDevice; // 声明一个蓝牙设备对象
    private BluetoothGatt mBluetoothGatt; // 声明一个蓝牙Gatt客户端对象
    private boolean isScaning = false;

    private UUID read_UUID_chara; // 读的特征编号
    private UUID read_UUID_service; // 读的服务编号
    private UUID write_UUID_chara; // 写的特征编号
    private UUID write_UUID_service; // 写的服务编号
    private UUID notify_UUID_chara; // 通知的特征编号
    private UUID notify_UUID_service; // 通知的服务编号
    private UUID indicate_UUID_chara; // 指示的特征编号
    private UUID indicate_UUID_service; // 指示的服务编号

    // Android12需要的蓝牙权限
    public String[] PERMISSIONS_12 = new String[]{
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT
    };

    // Android6需要的蓝牙权限
    public String[] PERMISSIONS_6 = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    public final int REQUEST_CODE_12 = 1;
    public final int REQUEST_CODE_6 = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_scan);
        // 初始化视图
        initView();
        // 初始化蓝牙适配器
        initBluetooth();
        if (BluetoothUtil.getBlueToothStatus()) { // 已经打开蓝牙
            ck_bluetooth.setChecked(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 立即校验权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12 校验权限
            PermissionUtil.checkPermission(this, PERMISSIONS_12, REQUEST_CODE_12);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Android6 权限校验
            PermissionUtil.checkPermission(this, PERMISSIONS_6, REQUEST_CODE_6);
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        ck_bluetooth = findViewById(R.id.ck_bluetooth);
        tv_discovery = findViewById(R.id.tv_discovery);
        lv_bluetooth = findViewById(R.id.lv_bluetooth);
        ck_bluetooth.setOnCheckedChangeListener(this);
        mListAdapter = new BlueListAdapter(this, mDeviceList);
        lv_bluetooth.setAdapter(mListAdapter);
        lv_bluetooth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BlueDevice item = mDeviceList.get(position);
                // 根据设备地址获取远端的蓝牙设备对象
                mRemoteDevice = mBluetoothAdapter.getRemoteDevice(item.address);
                Log.d(TAG, "onItemClick address="+mRemoteDevice.getAddress()+", name="+mRemoteDevice.getName());
                // 连接GATT服务器
                mBluetoothGatt = mRemoteDevice.connectGatt(BleScanActivity.this, false, mGattCallback);
            }
        });
    }

    /**
     * 初始化蓝牙适配器
     */
    private void initBluetooth() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "当前设备不支持低功耗蓝牙", Toast.LENGTH_SHORT).show();
            // 结束当前页面
            finish();
        }
        // 获取蓝牙管理器，并通过蓝牙管理器获取蓝牙适配器
        BluetoothManager bm = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bm.getAdapter(); // 获取蓝牙适配器
    }

    /**
     * 创建一个连接GATT客户端的回调对象
     */
    BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        /**
         * BLE连接的状态发生变化时回调
         * @param gatt
         * @param status
         * @param newState
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.d(TAG, "onConnectionStateChange status="+status+", newState="+newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // 开始查找GATT服务器提供的服务
                gatt.discoverServices();
                mHandler.post(mScanStop);
                runOnUiThread(() -> {
                    // 下面把找到的蓝牙设备添加到设备映射和设备列表中
                    BlueDevice device = mDeviceMap.get(mRemoteDevice.getAddress());
                    device.state = 1; // 定义在 BlueListAdapter.mBleStateArray 中，0-未连接  1-已连接
                    mDeviceMap.put(device.address, device);
                    mDeviceList.clear();
                    mDeviceList.addAll(mDeviceMap.values());
                    tv_discovery.setText("已连接"+mRemoteDevice.getName());
                    mListAdapter.notifyDataSetChanged();
                });

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) { // 连接断开
                // 关闭GATT客户端
                mBluetoothGatt.close();
            }
        }

        /**
         * 发现BLE服务端的服务列表及其特征值时回调
         * @param gatt
         * @param status
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.d(TAG, "onServicesDiscovered status"+status);
            if (status == BluetoothGatt.GATT_SUCCESS) { // 成功获取到GATT服务器
                // 获取GATT服务器提供的服务列表
                List<BluetoothGattService> serviceList = mBluetoothGatt.getServices();
                for (BluetoothGattService gattService : serviceList) { // 遍历服务
                    // 获取服务的特征值
                    List<BluetoothGattCharacteristic> charaList = gattService.getCharacteristics();
                    for (BluetoothGattCharacteristic chara : charaList) { // 遍历特征值
                        int charaProp = chara.getProperties(); // 获取该特征值的属性
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) { // 可读属性
                            read_UUID_chara = chara.getUuid();
                            read_UUID_service = gattService.getUuid();
                            Log.d(TAG, "read_chara=" + read_UUID_chara + ", read_service=" + read_UUID_service);
                        }
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) { // 可写,且需要应答。这个写操作是双向的，相当于等待反馈的写请求
                            write_UUID_chara = chara.getUuid();
                            write_UUID_service = gattService.getUuid();
                            Log.d(TAG, "write_chara=" + write_UUID_chara + ", write_service=" + write_UUID_service);
                        }
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) { // 可写，无需应答。这个写操作是单向的，相当于写命令
                            write_UUID_chara = chara.getUuid();
                            write_UUID_service = gattService.getUuid();
                            Log.d(TAG, "no_response write_chara=" + write_UUID_chara + ", write_service=" + write_UUID_service);
                        }
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) { // 支持通知
                            notify_UUID_chara = chara.getUuid();
                            notify_UUID_service = gattService.getUuid();
                            Log.d(TAG, "notify_chara=" + notify_UUID_chara + ", notify_service=" + notify_UUID_service);
                        }
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) { // 支持指示
                            indicate_UUID_chara = chara.getUuid();
                            indicate_UUID_service = gattService.getUuid();
                            Log.d(TAG, "indicate_chara=" + indicate_UUID_chara + ", indicate_service=" + indicate_UUID_service);
                        }
                    }
                }
            } else {
                Log.d(TAG, "onServicesDiscovered fail-->" + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.d(TAG, "onCharacteristicRead");
            if (status == BluetoothGatt.GATT_SUCCESS) {}
        }

        /**
         * 收到BLE服务端的数据变更时回调
         * @param gatt
         * @param chara
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic chara) {
            super.onCharacteristicChanged(gatt, chara);
            String message = new String(chara.getValue()); // 把服务端返回的数据转成字符串
            Log.d(TAG, "onCharacteristicChanged "+message);
        }

        /**
         * 收到BLE服务端的数据写入时回调
         * @param gatt
         * @param chara
         * @param status
         */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic chara, int status) {
            super.onCharacteristicWrite(gatt, chara, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                String message = new String(chara.getValue()); // 把服务端返回的数据转成字符串
                Log.d(TAG, "onCharacteristicWrite "+message);
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.d(TAG, "onDescriptorWrite");
            if (status == BluetoothGatt.GATT_SUCCESS) {}
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Log.d(TAG, "onReadRemoteRssi");
            if (status == BluetoothGatt.GATT_SUCCESS) {}
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Log.d(TAG, "onDescriptorRead");
            if ((status == BluetoothGatt.GATT_SUCCESS)) {}
        }
    };

    /**
     * Checkbox切换监听
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.ck_bluetooth) {
            if (isChecked) { // 开启了蓝牙功能
                ck_bluetooth.setText("蓝牙开");
                if (!BluetoothUtil.getBlueToothStatus()) {
                    // 开启蓝牙功能
                    BluetoothUtil.setBlueToothStatus(true);
                }
                mHandler.post(mScanStart); // 启动BLE扫描任务
            } else { // 关闭了蓝牙功能
                ck_bluetooth.setText("蓝牙关");
                mHandler.removeCallbacks(mScanStart); // 移除BLE扫描任务
                BluetoothUtil.setBlueToothStatus(false); // 关闭蓝牙功能
                // 清空列表数据
                mDeviceList.clear();
                // 通知适配器刷新列表
                mListAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 声明一个BLE设备扫描器的回调接口，并实现相关方法
     */
    ScanCallback mScanCallback = new ScanCallback() {
        /**
         * 接收到扫描结果会出发 OnScanResult 方法
         * @param callbackType
         * @param result
         */
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (TextUtils.isEmpty(result.getDevice().getName())) {
                return;
            }
            Log.d(TAG, "callbackType="+callbackType+", result="+result.toString());
            // 下面把找到的蓝牙设备添加到设备映射和设备列表
            BlueDevice device = new BlueDevice(result.getDevice().getName(), result.getDevice().getAddress(), 0);
            mDeviceMap.put(device.address, device);
            mDeviceList.clear();
            mDeviceList.addAll(mDeviceMap.values());
            runOnUiThread(() -> mListAdapter.notifyDataSetChanged());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    /**
     * 创建一个开启BLE扫描的任务
     */
    private Runnable mScanStart = new Runnable() {
        @Override
        public void run() {
            if (!isScaning && BluetoothUtil.getBlueToothStatus()) {
                isScaning = true;
                // 获取BLE设备扫描器
                BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
                // 开始扫描BLE设备，并设置回调
                scanner.startScan(mScanCallback);
                tv_discovery.setText("正在搜索低功耗蓝牙设备");
            } else {
                mHandler.postDelayed(this, 2000);
            }
        }
    };

    /**
     * 创建一个停止BLE扫描的任务
     */
    private Runnable mScanStop = new Runnable() {
        @Override
        public void run() {
            isScaning = false;
            // 获取BLE设备扫描器
            BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
            scanner.stopScan(mScanCallback); // 停止扫描BLE设备
            tv_discovery.setText("停止搜索低功耗蓝牙设备");
        }
    };

    /**
     * 权限校验结果回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_12:
                if (PermissionUtil.checkGrant(grantResults)) {
                    Log.d(TAG, "Android12版本及以上，获取蓝牙权限成功");
                } else {
                    Toast.makeText(this, "Android12版本及以上，权限获取失败", Toast.LENGTH_SHORT).show();
                    jumpToSettings();
                }
                break;
            case REQUEST_CODE_6:
                if (PermissionUtil.checkGrant(grantResults)) {
                    Log.d(TAG, "Android6版本及以上，获取蓝牙权限成功");
                } else {
                    Toast.makeText(this, "Android6版本及以上，权限获取失败", Toast.LENGTH_SHORT).show();
                    jumpToSettings();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mScanStart); // 移除开始BLE扫描的任务
        mHandler.removeCallbacks(mScanStop); // 移除停止BLE扫描的任务
        if (mBluetoothGatt != null) { // 断开客户端
            mBluetoothGatt.disconnect(); // 断开GATT连接
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