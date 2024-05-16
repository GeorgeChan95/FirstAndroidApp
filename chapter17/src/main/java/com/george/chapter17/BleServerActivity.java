package com.george.chapter17;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
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
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.george.chapter17.constant.BleConstant;
import com.george.chapter17.util.BluetoothUtil;
import com.george.chapter17.util.ChatUtil;
import com.george.chapter17.util.DateUtil;
import com.george.chapter17.util.PermissionUtil;
import com.george.chapter17.util.Utils;
import com.george.chapter17.util.ViewUtil;

import java.util.List;

@SuppressLint("MissingPermission")
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BleServerActivity extends AppCompatActivity {
    private static final String TAG = "GeorgeTag";
    private TextView tv_hint; // 声明一个文本视图对象
    private ScrollView sv_chat; // 声明一个滚动视图对象
    private LinearLayout ll_show; // 声明一个线性视图对象
    private LinearLayout ll_input; // 声明一个线性视图对象
    private EditText et_input; // 声明一个编辑框对象
    private Handler mHandler = new Handler(Looper.myLooper()); // 声明一个处理器对象
    private int dip_margin; // 每条聊天记录的四周空白距离
    private String mMinute = "00:00";

    private BluetoothManager mBluetoothManager; // 声明一个蓝牙管理器对象
    private BluetoothAdapter mBluetoothAdapter; // 声明一个蓝牙适配器对象
    private BluetoothDevice mRemoteDevice; // 声明一个蓝牙设备对象
    private BluetoothGattServer mGattServer; // 声明一个蓝牙GATT服务器对象
    private BluetoothGattCharacteristic mReadChara; // 客户端读取数据的特征值

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
        setContentView(R.layout.activity_ble_server);

        // 立即校验权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12 校验权限
            PermissionUtil.checkPermission(this, PERMISSIONS_12, REQUEST_CODE_12);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Android6 权限校验
            PermissionUtil.checkPermission(this, PERMISSIONS_6, REQUEST_CODE_6);
        }

        // 保持屏幕常量
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // 初始化视图
        initView();
        // 初始化蓝牙适配器
        initBluetooth();
        // 延迟200毫秒，打开服务端低功耗蓝牙广播
        mHandler.postDelayed(mAdvertise, 200);
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

    private Runnable mAdvertise = new Runnable() {
        @Override
        public void run() {
            if (BluetoothUtil.getBlueToothStatus()) { // 如果蓝牙已开启，往下执行
                String server_name = mBluetoothAdapter.getName();
                // 停止低功耗蓝牙广播
                stopAdvertise();
                // 开始低功耗蓝牙广播
                startAdvertise(server_name);
                tv_hint.setText("“"+server_name+"”服务端正在广播，请等候客户端连接");
            } else { // 如果蓝牙没有打开，则继续执行蓝牙打开
                mHandler.postDelayed(this, 200);
            }
        }
    };

    /**
     * 初始化蓝牙适配器
     */
    private void initBluetooth() {
        // 判断设备是否支持低功耗蓝牙，如果不支持则退出
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "当前设备不支持低功耗蓝牙", Toast.LENGTH_SHORT).show();
            finish();
        }
        // 获取蓝牙管理器，并从中获取到蓝牙适配器
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        // 获取蓝牙适配器
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        // 如果还未打开蓝牙，则开启蓝牙功能
        if (!BluetoothUtil.getBlueToothStatus()) {
            BluetoothUtil.setBlueToothStatus(true);
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        dip_margin = Utils.dip2px(this, 5);
        tv_hint = findViewById(R.id.tv_hint);
        sv_chat = findViewById(R.id.sv_chat);
        ll_show = findViewById(R.id.ll_show);
        ll_input = findViewById(R.id.ll_input);
        et_input = findViewById(R.id.et_input);
        // 发送按钮点击监听
        findViewById(R.id.btn_send).setOnClickListener(v -> sendMesssage());
    }

    /**
     * 发送聊天消息到客户端
     */
    private void sendMesssage() {
        // 获取输入的文本信息
        String message = et_input.getText().toString();
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "请先输入聊天消息", Toast.LENGTH_SHORT).show();
            return;
        }
        // 清空输入框
        et_input.setText("");
        // 隐藏输入框软键盘
        ViewUtil.hideOneInputMethod(this, et_input);
        List<String> msgList = ChatUtil.splitString(message, 20); // 按照20字节切片
        for (String msg : msgList) {
            mReadChara.setValue(msg); // 设置读特征值
            // 发送本地特征值已更新的通知
            mGattServer.notifyCharacteristicChanged(mRemoteDevice, mReadChara, false);
        }
        // 往聊天窗口添加聊天消息
        appendChatMsg(message, true);
    }

    /**
     * 停止低功耗蓝牙广播
     */
    private void stopAdvertise() {
        if (mBluetoothAdapter != null) {
            BluetoothLeAdvertiser advertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
            if (advertiser != null) {
                // 停止低功耗蓝牙广播
                advertiser.stopAdvertising(mAdvertiseCallback);
            }
        }
    }

    /**
     * 开启低功耗蓝牙广播
     * @param ble_name 蓝牙设备名称
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
        advertiser.startAdvertising(settings, advertiseData, mAdvertiseCallback);
    }

    /**
     * 创建一个低功耗蓝牙广播回调对象
     */
    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settings) {
            Log.d(TAG, "低功耗蓝牙广播成功："+settings.toString());
            addService(); // 添加读写服务UUID，特征值等
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.d(TAG, "低功耗蓝牙广播失败，错误代码为"+errorCode);
        }
    };

    /**
     * 添加读写服务UUID，特征值等
     */
    private void addService() {
        BluetoothGattService gattService = new BluetoothGattService(
                BleConstant.UUID_SERVER, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        // 只读的特征值
        mReadChara = new BluetoothGattCharacteristic(
                BleConstant.UUID_CHAR_READ,
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ);
        // 只写的特征值
        BluetoothGattCharacteristic charaWrite = new BluetoothGattCharacteristic(
                BleConstant.UUID_CHAR_WRITE,
                BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_WRITE);
        // 将特征值添加到服务中
        gattService.addCharacteristic(mReadChara);
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
                // 给远程设备对象赋值
                mRemoteDevice = device;
                String desc = String.format("%s\n已连接BLE客户端，对方名称为%s，MAC地址为%s",
                        tv_hint.getText().toString(), device.getName(), device.getAddress());
                runOnUiThread(() -> {
                    tv_hint.setText(desc);
                    ll_input.setVisibility(View.VISIBLE);
                });
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
         * @param chara
         * @param preparedWrite
         * @param responseNeeded
         * @param offset
         * @param value
         */
        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic chara, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, chara, preparedWrite, responseNeeded, offset, value);
            String message = new String(value); // 把客户端发来的数据转成字符串
            Log.d(TAG, "收到了客户端发过来的数据 " + message);
            // 使用BLE服务器对象发送应答，告诉它成功收到了要写入的数据
            mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, chara.getValue());
            runOnUiThread(() -> appendChatMsg(message, false)); // 往聊天窗口添加聊天消息
        }
    };

    // 往聊天窗口添加聊天消息
    private void appendChatMsg(String content, boolean isSelf) {
        appendNowMinute(); // 往聊天窗口添加当前时间
        // 把单条消息的线性布局添加到聊天窗口上
        ll_show.addView(ChatUtil.getChatView(this, content, isSelf));
        // 延迟100毫秒后启动聊天窗口的滚动任务
        new Handler(Looper.myLooper()).postDelayed(() -> {
            sv_chat.fullScroll(ScrollView.FOCUS_DOWN); // 滚动到底部
        }, 100);
    }

    // 往聊天窗口添加当前时间
    private void appendNowMinute() {
        String nowMinute = DateUtil.getNowMinute();
        if (!mMinute.substring(0, 4).equals(nowMinute.substring(0, 4))) {
            mMinute = nowMinute;
            ll_show.addView(ChatUtil.getHintView(this, nowMinute, dip_margin));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAdvertise(); // 停止低功耗蓝牙广播
        if (mGattServer != null) {
            mGattServer.close(); // 关闭GATT服务器
        }
    }
}