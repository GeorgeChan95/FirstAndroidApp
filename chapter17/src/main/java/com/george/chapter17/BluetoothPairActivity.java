package com.george.chapter17;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.Settings;
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
import java.util.List;
import java.util.Set;

@SuppressLint("MissingPermission")
public class BluetoothPairActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, AdapterView.OnItemClickListener {
    private static final String TAG = "GeorgeTag";

    // Android12需要的蓝牙权限
    public String[] PERMISSIONS_12 = new String[]{
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT
    };

    // Android6需要的蓝牙权限
    public String[] PERMISSIONS_6 = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    public final int REQUEST_CODE_12 = 1;
    public final int REQUEST_CODE_6 = 2;

    private List<BlueDevice> mDeviceList = new ArrayList<>(); // 蓝牙设备列表
    private CheckBox ck_bluetooth; // 声明一个复选框对象
    private TextView tv_discovery; // 声明一个文本视图对象
    private ListView lv_bluetooth; // 声明一个用于展示蓝牙设备的列表视图对象
    private BlueListAdapter bluelistAdapter; // 自定义蓝牙设备列表适配器
    private BluetoothAdapter bluetoothAdapter; // 蓝牙适配器对象
    private Handler mHandler = new Handler(Looper.myLooper()); // 声明一个处理器对象


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_pair);
        // 蓝牙开关控件
        ck_bluetooth = findViewById(R.id.ck_bluetooth);
        // 蓝牙搜索结果展示组件
        tv_discovery = findViewById(R.id.tv_discovery);
        // 蓝牙设备列表视图
        lv_bluetooth = findViewById(R.id.lv_bluetooth);

        // 设置蓝牙状态开关监听
        ck_bluetooth.setOnCheckedChangeListener(this);
        if (BluetoothUtil.getBlueToothStatus()) { // 蓝牙已打开
            ck_bluetooth.setChecked(true);
        }

        // 初始化蓝牙适配器
        initBluetooth();

        // 初始化蓝牙设备列表
        initBlueDevice();
    }

    /**
     * 初始化蓝牙适配器
     */
    private void initBluetooth() {
        // 获取系统默认的蓝牙适配器
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "当前设备未找到蓝牙功能", Toast.LENGTH_SHORT).show();
            // 关闭当前页面
            finish();
        }
    }

    /**
     * 初始化蓝牙设备列表
     */
    private void initBlueDevice() {
        mDeviceList.clear();
        // 获取当前已配对的蓝牙设备集合
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : bondedDevices) {
            mDeviceList.add(new BlueDevice(device.getName(), device.getAddress(), device.getBondState()));
        }

        // 初始化列表适配器
        if (bluelistAdapter == null) { // 首次打开页面，列表适配器为null，需要创建新的设备列表
            bluelistAdapter = new BlueListAdapter(this, mDeviceList);
            lv_bluetooth.setAdapter(bluelistAdapter);
            // 给列表项添加点击事件
            lv_bluetooth.setOnItemClickListener(this);
        } else { // 不是首次打开页面，则刷新列表数据
            bluelistAdapter.notifyDataSetChanged();
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

    @Override
    protected void onStart() {
        super.onStart();
        // 开始扫描周围的蓝牙设备
        mHandler.postDelayed(mRefresh, 50);

        // 需要过滤多个动作，则调用IntentFilter对象的addAction添加新动作
        IntentFilter discoveryFilter = new IntentFilter();
        discoveryFilter.addAction(BluetoothDevice.ACTION_FOUND); // 已发现远程设备
        discoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); // 本地蓝牙适配器已完成设备发现过程
        discoveryFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED); // 远程设备的绑定状态发生变化。例如，如果一个设备被绑定（配对）
        // 注册蓝牙设备搜索的广播接收器
        registerReceiver(discoveryReceiver, discoveryFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 取消蓝牙设备的搜索
        cancelDiscovery();
        // 注销蓝牙搜索广播接收器
        unregisterReceiver(discoveryReceiver);
    }

    /**
     * 定义一个刷新任务，每隔30秒执行一次蓝牙设备扫描
     */
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            // 开始扫描周围的蓝牙设备
            beginDiscovery();
            // 30秒执行一次设备扫描
            mHandler.postDelayed(this, 30*1000);
        }
    };

    /**
     * 开始搜索周围的蓝牙设备
     */
    private void beginDiscovery() {
        // 如果当前不是正在搜索，则开始新的搜索任务
        if (!bluetoothAdapter.isDiscovering() && BluetoothUtil.getBlueToothStatus()) {
            // 初始化蓝牙设备列表
            initBlueDevice();
            tv_discovery.setText("正在搜索蓝牙设备");
            // 扫描周围的蓝牙设备
            bluetoothAdapter.startDiscovery();
        }
    }

    /**
     * 取消蓝牙搜索
     */
    private void cancelDiscovery() {
        // 取消定时扫描蓝牙设备的任务
        mHandler.removeCallbacks(mRefresh);
        tv_discovery.setText("取消搜索蓝牙设备");
        if (bluetoothAdapter.isDiscovering()) {
            // 取消扫描周围的蓝牙设备
            bluetoothAdapter.cancelDiscovery();
        }
    }

    /**
     * 蓝牙设备扫描结果，通过广播返回
     */
    private BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "discoveryReceiver.onReceive: " + action);
            if (action.equals(BluetoothDevice.ACTION_FOUND)) { // 发现新的蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "name=" + device.getName() + ", state=" + device.getBondState());
                refreshDevice(device, device.getBondState());
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) { // 搜索完毕
                mHandler.removeCallbacks(mRefresh); // 需要持续搜索就要注释这行
                tv_discovery.setText("蓝牙设备搜索完成");
            } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) { // 配对状态变更
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    tv_discovery.setText("正在配对" + device.getName());
                } else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    tv_discovery.setText("完成配对" + device.getName());
                    mHandler.postDelayed(mRefresh, 50);
                } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    tv_discovery.setText("取消配对" + device.getName());
                    refreshDevice(device, device.getBondState()); // 刷新蓝牙设备列表
                }
            }
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

    /**
     * Checkbox切换回调方法
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.ck_bluetooth) {
            if (isChecked) { // 开启蓝牙功能
                ck_bluetooth.setText("蓝牙开");
                if (!BluetoothUtil.getBlueToothStatus()) { // 如果蓝牙打开
                    // 开启蓝牙功能
                    BluetoothUtil.setBlueToothStatus(true);
                }
                // 弹框提示用户是否允许其它设备发现当前设备
                mHandler.postDelayed(mDiscoverable, 100);
            } else { // 关闭蓝牙功能
                ck_bluetooth.setText("蓝牙关");
                // 取消蓝牙设备搜索
                cancelDiscovery();
                // 关闭蓝牙功能
                BluetoothUtil.setBlueToothStatus(false);
                // 初始化蓝牙设备列表
                initBlueDevice();
            }
        }
    }

    // 声明一个页面弹框回调函数
    ActivityResultLauncher<Intent> register = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        int resultCode = result.getResultCode();
        Intent intent = result.getData();
        if (intent != null && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "已成功设置允许当前设备被其它设备发现");
            Toast.makeText(this, "允许本地蓝牙被附近的其他蓝牙设备发现",
                    Toast.LENGTH_SHORT).show();
        } else if (intent != null && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "不允许蓝牙被附近的其他蓝牙设备发现",
                    Toast.LENGTH_SHORT).show();
        }
    });

    // 弹框提示用户是否允许其它设备发现当前设备
    private Runnable mDiscoverable = new Runnable() {
        @Override
        public void run() {
            // 从Android8.0开始，需要在蓝牙已开启的状态下，才能弹出选择框
            if (BluetoothUtil.getBlueToothStatus()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                register.launch(intent);
            }
        }
    };

    /**
     * 列表元素点击回调
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BlueDevice item = mDeviceList.get(position);
        // 根据设备地址，获取远端蓝牙设备对象
        BluetoothDevice blueDevice = bluetoothAdapter.getRemoteDevice(item.address);
        int bondState = blueDevice.getBondState();
        Log.d(TAG, "getBondState: " + blueDevice.getBondState() + "state: " + item.state);
        if (bondState == BluetoothDevice.BOND_NONE) { // 尚未配对
            boolean flag = BluetoothUtil.createBond(blueDevice);// 创建配对信息
            if (flag) {
                refreshDevice(blueDevice, BluetoothDevice.BOND_BONDED);
            }
        } else if (bondState == BluetoothDevice.BOND_BONDED) { // 已经配对了
            boolean flag = BluetoothUtil.removeBond(blueDevice); // 移除配对信息
            if (flag) {
                refreshDevice(blueDevice, BluetoothDevice.BOND_NONE);
            }
        }
    }


    /**
     * 刷新蓝牙设备列表
     * @param device 操作的蓝牙设备对象
     * @param state 连接状态
     */
    private void refreshDevice(BluetoothDevice device, int state) {
        int i;
        for (i = 0; i < mDeviceList.size(); i++) {
            BlueDevice item = mDeviceList.get(i);
            if (item.address.equals(device)) {
                item.state = state;
                // 列表数据
                mDeviceList.set(i, item);
            }
        }
        // 当前设备不在已扫描的设备列表中，是一个新的设备数据
        if (i >= mDeviceList.size()) {
            mDeviceList.add(new BlueDevice(device.getName(), device.getAddress(), device.getBondState()));
        }
        // 通知列表适配器刷新
        bluelistAdapter.notifyDataSetChanged();
    }
}