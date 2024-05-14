package com.george.chapter17.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.george.chapter17.util.BluetoothConnector;
import com.george.chapter17.util.BluetoothUtil;

import java.io.IOException;

public class BlueAcceptTask extends Thread {
    private static final String TAG = "GeorgeTag";

    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";
    private static BluetoothServerSocket mServerSocket; // 蓝牙服务端Socket
    private Activity mAct; // 声明页面活动实例
    private BlueAcceptListener mListener;// 声明一个蓝牙侦听的监听器对象

    @SuppressLint("MissingPermission")
    public BlueAcceptTask(Activity act, boolean secure, BlueAcceptListener listener) {
        this.mAct = act;
        this.mListener = listener;
        Log.d(TAG, "初始化蓝牙服务端");
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // 以下提供了三种侦听方法，使得在不同情况下都能获得服务端的Socket对象
        try {
            if (mServerSocket != null) {
                mServerSocket.close();
            }
            if (secure) { // 安全连接
                mServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, BluetoothConnector.uuid);
            } else { // 不安全连接
                mServerSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME_INSECURE, BluetoothConnector.uuid);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 创建一个默认频道的监听
            mServerSocket = BluetoothUtil.listenServer(bluetoothAdapter);
        }
    }

    @Override
    public void run() {
        Log.d(TAG, "当前执行 BlueAcceptTask.run 方法 ......");
        while (true) {
            try {
                // 阻塞等待设备连接请求
                BluetoothSocket socket = mServerSocket.accept();
                if (socket != null) {
                    mAct.runOnUiThread(() -> mListener.onBlueAccept(socket));
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


    /**
     * 定义一个蓝牙侦听的监听器接口，在获得响应之后回调onBlueAccept方法
     */
    public interface BlueAcceptListener {
        /**
         * 获取连接响应后调用此方法
         * @param socket 蓝牙客户端的Socket
         */
        void onBlueAccept(BluetoothSocket socket);
    }
}
