package com.george.chapter17;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.george.chapter17.constant.BleConstant;
import com.george.chapter17.util.BluetoothUtil;
import com.george.chapter17.util.PermissionUtil;

@SuppressLint("MissingPermission")
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BleAdvertiseActivity extends AppCompatActivity {

    private static final String TAG = "GeorgeTag";

    private CheckBox ck_bluetooth; // 声明一个复选框对象
    private EditText et_name; // 声明一个编辑框对象
    private Button btn_advertise; // 声明一个按钮对象
    private TextView tv_hint; // 声明一个文本视图对象

    private BluetoothManager mBluetoothManager; // 声明一个蓝牙管理器对象
    private BluetoothAdapter mBluetoothAdapter; // 声明一个蓝牙适配器对象
    private BluetoothGattServer mGattServer; // 声明一个蓝牙GATT服务器对象
    private boolean isAdvertising = false; // 是否正在广播

    // Android12需要的蓝牙权限
    public String[] PERMISSIONS_12 = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE
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
        setContentView(R.layout.activity_ble_advertise);

        // 立即校验权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12 校验权限
            PermissionUtil.checkPermission(this, PERMISSIONS_12, REQUEST_CODE_12);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Android6 权限校验
            PermissionUtil.checkPermission(this, PERMISSIONS_6, REQUEST_CODE_6);
        }

        initView(); // 初始化视图
        initBluetooth(); // 初始化蓝牙适配器
        if (BluetoothUtil.getBlueToothStatus()) { // 已经打开蓝牙
            ck_bluetooth.setChecked(true);
        }
    }

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

    /**
     * 初始化视图
     */
    private void initView() {
        ck_bluetooth = findViewById(R.id.ck_bluetooth);
        et_name = findViewById(R.id.et_name);
        btn_advertise = findViewById(R.id.btn_advertise);
        tv_hint = findViewById(R.id.tv_hint);

        // 给蓝牙开关CheckBox设置监听
        ck_bluetooth.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) { // 蓝牙功能开启
                ck_bluetooth.setText("蓝牙开");
                btn_advertise.setEnabled(true);
                btn_advertise.setText("发送低功耗蓝牙广播");
                if (!BluetoothUtil.getBlueToothStatus()) { // 还未打开蓝牙
                    BluetoothUtil.setBlueToothStatus(true); // 开启蓝牙功能
                }
            } else { // 关闭蓝牙功能
                ck_bluetooth.setText("蓝牙关");
                btn_advertise.setEnabled(false);
                isAdvertising = false;
                BluetoothUtil.setBlueToothStatus(false); // 关闭蓝牙
            }
        });

        // 发送按钮默认不能点击
        btn_advertise.setEnabled(false);
        // 给发送按钮设置点击监听
        btn_advertise.setOnClickListener(v -> {
            if (!BluetoothUtil.getBlueToothStatus()) { // 还未打开蓝牙
                Toast.makeText(this, "请先开启蓝牙再发送低功耗蓝牙广播", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(et_name.getText().toString())) {
                Toast.makeText(this, "请先输入服务器名称", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isAdvertising) {
                stopAdvertise(); // 停止低功耗蓝牙广播
            } else {
                startAdvertise(et_name.getText().toString()); // 开始低功耗蓝牙广播
            }
            isAdvertising = !isAdvertising;
            btn_advertise.setText(isAdvertising ? "停止低功耗蓝牙广播" : "发送低功耗蓝牙广播");
        });
    }

    /**
     * 初始化蓝牙适配器
     */
    private void initBluetooth() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "当前设备不支持低功耗蓝牙", Toast.LENGTH_SHORT).show();
            finish(); // 关闭当前页面
        }
        // 获取蓝牙设备管理器，并从中得到蓝牙适配器
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
    }

    /**
     * 开始低功耗蓝牙广播
     * @param ble_name 设备名称
     */
    private void startAdvertise(String ble_name) {
        // 设置广播参数
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setConnectable(true) // 是否允许连接
                .setTimeout(0) // 设置广播超时时间为0，表示广播永久有效
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH) // 设置广播的传输功率级别为高
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY) // 设置广播模式为低延迟模式，以最小化广播的延迟。
                .build();
        AdvertiseData advertiseData = new AdvertiseData.Builder()
                .setIncludeDeviceName(true) // 设置要广播的数据中包含设备名称
                .setIncludeTxPowerLevel(true) // 设置要广播的数据中包含传输功率级别
                .build();
        // 设置蓝牙名称
        mBluetoothAdapter.setName(ble_name);
        // 获取BLE广播器
        BluetoothLeAdvertiser advertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        // 发送BLE服务端广播
        advertiser.startAdvertising(settings, advertiseData, advertiseCallback);
    }

    /**
     * 停止低功耗蓝牙广播
     */
    private void stopAdvertise() {
        if (mBluetoothAdapter != null) {
            // 获取BLE广播器
            BluetoothLeAdvertiser advertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
            if (advertiser != null) {
                // 停止低功耗蓝牙广播
                advertiser.stopAdvertising(advertiseCallback);
            }
        }
    }

    /**
     * 发送BLE服务端广播的回调接口，并实现其方法
     */
    AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
        /**
         * 低功耗蓝牙广播发送成功的回调
         * @param settings
         */
        @Override
        public void onStartSuccess(AdvertiseSettings settings) {
            Log.d(TAG, "低功耗蓝牙广播成功："+settings.toString());
            // 添加读写服务UUID，特征值等
            addService();
            String desc = String.format("BLE服务端“%s”正在对外广播", et_name.getText().toString());
            tv_hint.setText(desc);
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            Log.d(TAG, "低功耗蓝牙广播失败，错误代码为"+errorCode);
            tv_hint.setText("低功耗蓝牙广播失败，错误代码为"+errorCode);
        }
    };

    /**
     * 添加读写服务UUID，特征值等
     */
    private void addService() {
        BluetoothGattService gattService = new BluetoothGattService(
                BleConstant.UUID_SERVER, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        // 只读的特征值
        BluetoothGattCharacteristic charaRead = new BluetoothGattCharacteristic(
                BleConstant.UUID_CHAR_READ,
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ);
        // 只写的特征值
        BluetoothGattCharacteristic charaWrite = new BluetoothGattCharacteristic(
                BleConstant.UUID_CHAR_WRITE,
                BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_WRITE);
        // 将特征值添加到服务中
        gattService.addCharacteristic(charaRead);
        gattService.addCharacteristic(charaWrite);
        // 开启GATT服务器等待客户端连接
        mGattServer = mBluetoothManager.openGattServer(this, serverCallback);
        // 向GATT服务器添加指定的方法
        mGattServer.addService(gattService);
    }

    /**
     * 创建一个GATT服务器回调对象，重写其方法
     */
    BluetoothGattServerCallback serverCallback = new BluetoothGattServerCallback() {
        /**
         * BLE连接的状态发生变化时回调
         * @param device
         * @param status
         * @param newState
         */
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            Log.d(TAG, "onConnectionStateChange device=" + device.toString() + " status=" + status + " newState=" + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) { // 连接成功
                String desc = String.format("%s\n已连接BLE客户端，对方名称为%s，MAC地址为%s",
                        tv_hint.getText().toString(), device.getName(), device.getAddress());
                runOnUiThread(() -> tv_hint.setText(desc));
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                String desc = String.format("%s\n与BLE客户端断开连接，对方名称为%s，MAC地址为%s",
                        tv_hint.getText().toString(), device.getName(), device.getAddress());
                runOnUiThread(() -> tv_hint.setText(desc));
            }
        }

        /**
         * 收到BLE客户端写入请求时回调
         * @param device
         * @param requestId
         * @param characteristic
         * @param preparedWrite
         * @param responseNeeded
         * @param offset
         * @param value
         */
        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
            String message = new String(value); // 把客户端发来的数据转成字符串
            Log.d(TAG, "收到了客户端发过来的数据 " + message);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 停止低功耗蓝牙广播
        stopAdvertise();
        // 关闭GATT服务器
        if (mGattServer != null) {
            mGattServer.close();
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