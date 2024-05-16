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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.george.chapter17.adapter.BlueListAdapter;
import com.george.chapter17.constant.BleConstant;
import com.george.chapter17.entity.BlueDevice;
import com.george.chapter17.util.BluetoothUtil;
import com.george.chapter17.util.ChatUtil;
import com.george.chapter17.util.DateUtil;
import com.george.chapter17.util.PermissionUtil;
import com.george.chapter17.util.Utils;
import com.george.chapter17.util.ViewUtil;
import com.george.chapter17.widget.NoScrollListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressLint("MissingPermission")
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BleClientActivity extends AppCompatActivity {
    private static final String TAG = "GeorgeTag";

    private TextView tv_hint; // 声明一个文本视图对象
    private ScrollView sv_chat; // 声明一个滚动视图对象
    private LinearLayout ll_show; // 声明一个线性视图对象
    private LinearLayout ll_input; // 声明一个线性视图对象
    private EditText et_input; // 声明一个编辑框对象
    private Handler mHandler = new Handler(Looper.myLooper()); // 声明一个处理器对象
    private int dip_margin; // 每条聊天记录的四周空白距离
    private String mMinute = "00:00";

    private NoScrollListView nslv_device; // 声明一个不滚动列表视图对象
    private BlueListAdapter mListAdapter; // 声明一个蓝牙设备的列表适配器对象
    private Map<String, BlueDevice> mDeviceMap = new HashMap<>(); // 蓝牙设备映射
    private List<BlueDevice> mDeviceList = new ArrayList<>(); // 蓝牙设备列表

    private BluetoothAdapter mBluetoothAdapter; // 声明一个蓝牙适配器对象
    private BluetoothDevice mRemoteDevice; // 声明一个蓝牙设备对象
    private BluetoothGatt mBluetoothGatt; // 声明一个蓝牙GATT客户端对象

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
        setContentView(R.layout.activity_ble_client);

        // 立即校验权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12 校验权限
            PermissionUtil.checkPermission(this, PERMISSIONS_12, REQUEST_CODE_12);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Android6 权限校验
            PermissionUtil.checkPermission(this, PERMISSIONS_6, REQUEST_CODE_6);
        }

        initView(); // 初始化视图
        initBluetooth(); // 初始化蓝牙适配器
        mHandler.postDelayed(mScanStart, 200);
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
        dip_margin = Utils.dip2px(this, 5);
        tv_hint = findViewById(R.id.tv_hint);
        sv_chat = findViewById(R.id.sv_chat);
        ll_show = findViewById(R.id.ll_show);
        ll_input = findViewById(R.id.ll_input);
        et_input = findViewById(R.id.et_input);
        nslv_device = findViewById(R.id.nslv_device);
        findViewById(R.id.btn_send).setOnClickListener(v -> sendMesssage());
        mListAdapter = new BlueListAdapter(this, mDeviceList);
        nslv_device.setAdapter(mListAdapter);
        nslv_device.setOnItemClickListener((parent, view, position, id) -> {
            BlueDevice item = mDeviceList.get(position);
            // 根据设备地址获取远端的蓝牙设备对象
            mRemoteDevice = mBluetoothAdapter.getRemoteDevice(item.address);
            Log.d(TAG, "onItemClick address="+mRemoteDevice.getAddress()+", name="+mRemoteDevice.getName());
            // 连接GATT服务器
            mBluetoothGatt = mRemoteDevice.connectGatt(BleClientActivity.this, false, mGattCallback);
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
        // 获取蓝牙管理器，并从中得到蓝牙适配器
        BluetoothManager bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bm.getAdapter(); // 获取蓝牙适配器
        if (!BluetoothUtil.getBlueToothStatus()) { // 还未打开蓝牙
            BluetoothUtil.setBlueToothStatus(true); // 开启蓝牙功能
        }
    }

    // 创建一个开启BLE扫描的任务
    private Runnable mScanStart = new Runnable() {
        @Override
        public void run() {
            if (BluetoothUtil.getBlueToothStatus()) { // 已经打开蓝牙
                // 获取BLE设备扫描器
                BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
                scanner.startScan(mScanCallback); // 开始扫描BLE设备
            } else {
                mHandler.postDelayed(this, 2000);
            }
        }
    };

    /**
     * 发送聊天消息
     */
    private void sendMesssage() {
        String message = et_input.getText().toString();
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "请先输入聊天消息", Toast.LENGTH_SHORT).show();
            return;
        }
        // 清空输入框
        et_input.setText("");
        ViewUtil.hideOneInputMethod(this, et_input); // 隐藏软键盘
        // 启动消息发送线程
        new MessageThread(message).start();
        appendChatMsg(message, true); // 往聊天窗口添加聊天消息
    }

    /**
     * 定义一个消息发送线程
     */
    private class MessageThread extends Thread {
        private List<String> msgList; // 消息列表
        public MessageThread(String message) {
            msgList = ChatUtil.splitString(message, 20);
        }

        @Override
        public void run() {
            // 拿到写的特征值
            BluetoothGattCharacteristic chara = mBluetoothGatt.getService(BleConstant.UUID_SERVER)
                    .getCharacteristic(BleConstant.UUID_CHAR_WRITE);
            for (int i=0; i<msgList.size(); i++) {
                if (isLastSuccess) { // 需要等到上一条回调成功之后，才能发送下一条消息
                    isLastSuccess = false;
                    Log.d(TAG, "writeCharacteristic "+msgList.get(i));
                    chara.setValue(msgList.get(i)); // 设置写特征值
                    // 往GATT服务器写入特征值
                    mBluetoothGatt.writeCharacteristic(chara);
                } else {
                    i--;
                }
                try {
                    sleep(300); // 休眠300毫秒，等待上一条的回调通知
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isLastSuccess = true; // 上一条消息是否发送成功

    private UUID read_UUID_chara; // 读的特征编号
    private UUID read_UUID_service; // 读的服务编号
    private UUID write_UUID_chara; // 写的特征编号
    private UUID write_UUID_service; // 写的服务编号
    private UUID notify_UUID_chara; // 通知的特征编号
    private UUID notify_UUID_service; // 通知的服务编号
    private UUID indicate_UUID_chara; // 指示的特征编号
    private UUID indicate_UUID_service; // 指示的服务编号

    /**
     * 创建一个GATT客户端回调对象
     */
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
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
            if (newState == BluetoothProfile.STATE_CONNECTED) { // 连接成功
                gatt.discoverServices(); // 开始查找GATT服务器提供的服务
                // 获取BLE设备扫描器
                BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
                scanner.stopScan(mScanCallback); // 停止扫描BLE设备
                runOnUiThread(() -> {
                    String desc = String.format("已连接BLE服务端，对方名称为“%s”，MAC地址为%s",
                            mRemoteDevice.getName(), mRemoteDevice.getAddress());
                    tv_hint.setText(desc);
                    ll_input.setVisibility(View.VISIBLE);
                    nslv_device.setVisibility(View.GONE);
                });
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) { // 连接断开
                mBluetoothGatt.close(); // 关闭GATT客户端
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
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // 获得特征值的第一种办法：直接问硬件厂商，然后把特征值写在代码中
//                BluetoothGattService service = mBluetoothGatt.getService(BleConstant.UUID_SERVER);
//                if (service == null) {
//                    Log.d(TAG, "onServicesDiscovered service is null");
//                    return;
//                }
//                BluetoothGattCharacteristic chara1 = service.getCharacteristic(BleConstant.UUID_CHAR_READ);
//                boolean b = mBluetoothGatt.setCharacteristicNotification(chara1, true);
//                Log.d(TAG, "onServicesDiscovered 设置通知 " + b);
                // 获得特征值的第二种办法：通过特征属性的匹配关系，寻找对应的各路特征值
                List<BluetoothGattService> gattServiceList= mBluetoothGatt.getServices();
                for (BluetoothGattService gattService : gattServiceList) {
                    List<BluetoothGattCharacteristic> charaList = gattService.getCharacteristics();
                    for (BluetoothGattCharacteristic chara : charaList) {
                        int charaProp = chara.getProperties(); // 获取该特征的属性
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            read_UUID_chara = chara.getUuid();
                            read_UUID_service = gattService.getUuid();
                            Log.d(TAG, "read_chara=" + read_UUID_chara + ", read_service=" + read_UUID_service);
                        }
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                            write_UUID_chara = chara.getUuid();
                            write_UUID_service = gattService.getUuid();
                            Log.d(TAG,"write_chara="+write_UUID_chara+", write_service="+write_UUID_service);
                        }
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
                            write_UUID_chara = chara.getUuid();
                            write_UUID_service = gattService.getUuid();
                            Log.d(TAG,"no_response write_chara="+write_UUID_chara+", write_service="+write_UUID_service);
                        }
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            notify_UUID_chara = chara.getUuid();
                            notify_UUID_service = gattService.getUuid();
                            Log.d(TAG,"notify_chara="+notify_UUID_chara+", notify_service="+notify_UUID_service);
                        }
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
                            indicate_UUID_chara = chara.getUuid();
                            indicate_UUID_service = gattService.getUuid();
                            Log.d(TAG,"indicate_chara="+indicate_UUID_chara+", indicate_service="+indicate_UUID_service);
                        }
                    }
                    BluetoothGattService service = mBluetoothGatt.getService(read_UUID_service);
                    if (read_UUID_service != null) {
                        BluetoothGattCharacteristic chara = service.getCharacteristic(read_UUID_chara);
                        // 开启或关闭特征值的通知（第二个参数为true表示开启）
                        boolean b = mBluetoothGatt.setCharacteristicNotification(chara, true);
                        Log.d(TAG, "onServicesDiscovered 设置通知 " + b);
                    }
                }
            } else {
                Log.d(TAG, "onServicesDiscovered fail-->" + status);
            }
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
            runOnUiThread(() ->appendChatMsg(message, false)); // 往聊天窗口添加聊天消息
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
            Log.d(TAG, "接收到服务端数据写入回调方法：onCharacteristicWrite status="+status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                isLastSuccess = true;
            } else {
                Log.d(TAG, "write fail->" + status);
            }
        }

        /**
         * 收到BLE服务端的读特征值时回调
         * @param gatt
         * @param chara
         * @param status
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic chara, int status) {
            super.onCharacteristicRead(gatt, chara, status);
            Log.d(TAG, "接收到服务端读特征值的回调方法：onCharacteristicRead status="+status);
            String message = new String(chara.getValue()); // 把服务端返回的数据转成字符串
            Log.d(TAG, "onCharacteristicChanged "+message);
            runOnUiThread(() ->appendChatMsg(message, false)); // 往聊天窗口添加聊天消息
        }
    };

    /**
     * 创建一个扫描回调对象
     */
    private ScanCallback mScanCallback = new ScanCallback() {
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
     * 往聊天窗口添加聊天消息
     * @param content
     * @param isSelf
     */
    private void appendChatMsg(String content, boolean isSelf) {
        appendNowMinute(); // 往聊天窗口添加当前时间
        // 把单条消息的线性布局添加到聊天窗口上
        ll_show.addView(ChatUtil.getChatView(this, content, isSelf));
        // 延迟100毫秒后启动聊天窗口的滚动任务
        new Handler(Looper.myLooper()).postDelayed(() -> {
            sv_chat.fullScroll(ScrollView.FOCUS_DOWN); // 滚动到底部
        }, 100);
    }

    /**
     * 往聊天窗口添加当前时间
     */
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
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect(); // 断开GATT连接
        }
    }
}