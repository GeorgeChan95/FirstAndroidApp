package com.george.chapter17;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.george.chapter17.adapter.BlueListAdapter;
import com.george.chapter17.entity.BlueDevice;
import com.george.chapter17.task.BlueAcceptTask;
import com.george.chapter17.task.BlueConnectTask;
import com.george.chapter17.task.BlueReceiveTask;
import com.george.chapter17.util.BluetoothUtil;
import com.george.chapter17.util.PermissionUtil;
import com.george.chapter17.widget.InputDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressLint("MissingPermission")
public class BluetoothTransActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, AdapterView.OnItemClickListener {
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

    private CheckBox ck_bluetooth; // 声明一个复选框对象
    private TextView tv_discovery; // 声明一个文本视图对象
    private ListView lv_bluetooth; // 声明一个用于展示蓝牙设备的列表视图对象
    private Button btn_refresh; // 刷新按钮控件
    private BluetoothAdapter mBluetooth; // 声明一个蓝牙适配器对象
    private BlueListAdapter mListAdapter; // 声明一个蓝牙设备的列表适配器对象
    private List<BlueDevice> mDeviceList = new ArrayList<>(); // 蓝牙设备列表
    private List<String> mAddressList = new ArrayList<>(); // 蓝牙设备地址列表
    private int mOpenCode = 1; // 是否允许扫描蓝牙设备的选择对话框返回结果代码
    private Map<String, Integer> mMapState = new HashMap<>(); // 蓝牙状态映射
    private Handler mHandler = new Handler(Looper.myLooper()); // 声明一个处理器对象
    private BluetoothSocket mBlueSocket; // 声明一个蓝牙客户端套接字对象


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_trans);
        initBluetooth(); // 初始化蓝牙适配器
        ck_bluetooth = findViewById(R.id.ck_bluetooth);
        tv_discovery = findViewById(R.id.tv_discovery);
        lv_bluetooth = findViewById(R.id.lv_bluetooth);

        // 蓝牙开关选项的监听
        ck_bluetooth.setOnCheckedChangeListener(this);

        // 已经打开蓝牙，将checkbox置为开启状态
        if (BluetoothUtil.getBlueToothStatus()) {
            ck_bluetooth.setChecked(true);
        }
        /**
         * 初始化蓝牙设备列表
         */
        initBlueDevice();

        // 刷新按钮点击回调
        findViewById(R.id.btn_refresh).setOnClickListener(v -> mHandler.postDelayed(mRefresh, 50));
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
        discoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);//蓝牙开始搜索
        // 注册蓝牙设备搜索的广播接收器
        registerReceiver(discoveryReceiver, discoveryFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelDiscovery(); // 取消蓝牙设备的搜索
        unregisterReceiver(discoveryReceiver); // 注销蓝牙设备搜索的广播接收器
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBlueSocket != null) {
            try {
                // 关闭蓝牙套接字
                mBlueSocket.close();;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 蓝牙开关选项切换监听
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
                // 监听连接请求
                mHandler.postDelayed(mAccept, 1000); // 服务端需要，客户端不需要（但是一个Android，可以同时做客户端和服务端，所以都可以加上监听连接请求的方法）
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

    /**
     * 列表项点击监听
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BlueDevice item = mDeviceList.get(position);
        // 根据设备地址获得远端的蓝牙设备对象
        BluetoothDevice device = mBluetooth.getRemoteDevice(item.address);
        Log.d(TAG, "getBondState="+device.getBondState()+", item.state="+item.state);
        if (device.getBondState() == BluetoothDevice.BOND_NONE) { // 尚未配对
            BluetoothUtil.createBond(device); // 创建配对信息
        } else if (device.getBondState() == BluetoothDevice.BOND_BONDED &&
                item.state != BlueListAdapter.CONNECTED) { // 已经配对但尚未连接
            tv_discovery.setText("开始连接");
            // 创建一个蓝牙设备连接线程，成功连接就启动蓝牙消息的接收任务
            BlueConnectTask connectTask = new BlueConnectTask(this, device, socket -> startReceiveTask(socket));
            connectTask.start();
        } else if (device.getBondState() == BluetoothDevice.BOND_BONDED &&
                item.state == BlueListAdapter.CONNECTED) { // 已经配对且已经连接
            tv_discovery.setText("正在发送消息");
            // 弹出消息输入对话框。准备向对方发送消息，往蓝牙设备套接字中写入消息数据
            InputDialog dialog = new InputDialog(this, "", 0, "请输入要发送的消息", (idt, content, seq) -> {
                BluetoothUtil.writeOutputStream(mBlueSocket, content);
            });
            dialog.show();
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
     * 定时监听蓝牙连接请求
     */
    private Runnable mAccept = new Runnable() {
        @Override
        public void run() {
            if (mBluetooth.getState() == BluetoothAdapter.STATE_ON) { // 如果蓝牙已打开
                // 创建一个蓝牙设备侦听线程，成功侦听就启动蓝牙消息的接收任务
                BlueAcceptTask acceptTask = new BlueAcceptTask(BluetoothTransActivity.this, true, socket -> startReceiveTask(socket));
                acceptTask.start();
            } else {
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    /**
     * 启动蓝牙消息的接收任务
     * @param socket 蓝牙客户端连接
     */
    private void startReceiveTask(BluetoothSocket socket) {
        if (socket == null) {
            return;
        }
        tv_discovery.setText("连接成功");
        mBlueSocket = socket;
        // 刷新设备列表
        refreshDevice(mBlueSocket.getRemoteDevice(), BlueListAdapter.CONNECTED);
        // 创建一个蓝牙消息的接收线程
        BlueReceiveTask receiveTask = new BlueReceiveTask(this, mBlueSocket, message -> {
            if (!TextUtils.isEmpty(message)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("收到消息");
                builder.setMessage(message);
                builder.setPositiveButton("确定", null);
                builder.create().show();
            }
        });
        receiveTask.start();
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
     * 定义一个刷新任务，每隔30秒执行一次蓝牙设备扫描
     */
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            // 开始扫描周围的蓝牙设备
            beginDiscovery();
        }
    };

    /**
     * 初始化蓝牙设备列表
     */
    private void initBlueDevice() {
        mDeviceList.clear();
        mAddressList.clear();
        // 获取已经配对的蓝牙设备集合
        Set<BluetoothDevice> bondedDevices = mBluetooth.getBondedDevices();
        for (BluetoothDevice device : bondedDevices) {
            int state = mMapState.containsKey(device.getAddress()) ?
                    mMapState.get(device.getAddress()) : device.getBondState();
            mDeviceList.add(new BlueDevice(device.getName(), device.getAddress(), state));
        }
        if (mListAdapter == null) { // 首次打开页面，则创建一个新的蓝牙设备列表
            mListAdapter = new BlueListAdapter(this, mDeviceList);
            lv_bluetooth.setAdapter(mListAdapter);
            lv_bluetooth.setOnItemClickListener(this);
        } else { // 不是首次打开页面，则刷新蓝牙设备列表
            mListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 初始化蓝牙适配器
     */
    private void initBluetooth() {
        // 获取系统默认的蓝牙适配器
        mBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (mBluetooth == null) {
            Toast.makeText(this, "当前设备未找到蓝牙功能", Toast.LENGTH_SHORT).show();
            finish(); // 关闭当前页面
        }
    }

    /**
     * 开始搜索周围的蓝牙设备
     */
    public void beginDiscovery() {
        // 如果当前不是正在搜索，则开始新的搜索任务
        if (!mBluetooth.isDiscovering() && BluetoothUtil.getBlueToothStatus()) {
            // 初始化蓝牙设备列表
            initBlueDevice();
            tv_discovery.setText("正在搜索蓝牙设备");
            // 扫描周围的蓝牙设备
            mBluetooth.startDiscovery();
        }
    }

    // 取消蓝牙设备的搜索
    private void cancelDiscovery() {
        mHandler.removeCallbacks(mRefresh);
        tv_discovery.setText("取消搜索蓝牙设备");
        // 当前正在搜索，则取消搜索任务
        if (mBluetooth.isDiscovering()) {
            mBluetooth.cancelDiscovery(); // 取消扫描周围的蓝牙设备
        }
    }

    /**
     * 蓝牙设备的搜索结果通过广播返回
     */
    private BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive action=" + action);
            // 获得已经搜索到的蓝牙设备
            if (action.equals(BluetoothDevice.ACTION_FOUND)) { // 发现新的蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "name=" + device.getName() + ", state=" + device.getBondState());
                refreshDevice(device, device.getBondState()); // 将发现的蓝牙设备加入到设备列表
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) { // 搜索完毕
                //mHandler.removeCallbacks(mRefresh); // 需要持续搜索就要注释这行
                tv_discovery.setText("蓝牙设备搜索完成");
            } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) { // 配对状态变更
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    tv_discovery.setText("正在配对" + device.getName());
                } else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    tv_discovery.setText("完成配对" + device.getName());
                    mHandler.postDelayed(mRefresh, 50);
//                    refreshDevice(device, device.getBondState());
                } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    tv_discovery.setText("取消配对" + device.getName());
                    refreshDevice(device, device.getBondState()); // 刷新蓝牙设备列表
                }
            }
        }
    };

    /**
     * 刷新蓝牙设备列表
     * @param device
     * @param state
     */
    private void refreshDevice(BluetoothDevice device, int state) {
        for (int i = 0; i < mDeviceList.size(); i++) {
            BlueDevice item = mDeviceList.get(i);
            if (item.address.equals(device.getAddress())) {
                if (item.state != BlueListAdapter.CONNECTED) {
                    item.state = state;
                    mDeviceList.set(i, item);
                    mMapState.put(item.address, state);
                }
            } else {
                if (!mAddressList.contains(device.getAddress())) {
                    mAddressList.add(device.getAddress());
                    mDeviceList.add(new BlueDevice(device.getName(), device.getAddress(), state));
                }
            }
        }
        // 通知列表适配器刷新页面
        mListAdapter.notifyDataSetChanged();
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