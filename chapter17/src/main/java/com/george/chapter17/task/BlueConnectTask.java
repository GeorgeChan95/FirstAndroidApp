package com.george.chapter17.task;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.george.chapter17.util.BluetoothConnector;

import java.io.IOException;

public class BlueConnectTask extends Thread {
    private static final String TAG = "BlueConnectTask";
    private Activity mAct; // 声明一个活动实例
    private BlueConnectListener mListener; // 声明一个蓝牙连接的监听器对象
    private BluetoothDevice mDevice; // 声明一个蓝牙设备对象

    public BlueConnectTask(Activity act, BluetoothDevice device, BlueConnectListener listener) {
        mAct = act;
        mListener = listener;
        mDevice = device;
    }

    @Override
    public void run() {
        BluetoothConnector connector = new BluetoothConnector(mDevice, true, BluetoothAdapter.getDefaultAdapter(), null);
        try {
            // 开始连接，并返回对方设备的蓝牙套接字对象BluetoothSocket
            BluetoothSocket socket = connector.connect().getUnderlyingSocket();
            mAct.runOnUiThread(() -> mListener.onBlueConnect(socket));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 定义一个蓝牙连接的监听器接口，用于在成功连接之后调用onBlueConnect方法
     */
    public interface BlueConnectListener {
        void onBlueConnect(BluetoothSocket socket);
    }
}
