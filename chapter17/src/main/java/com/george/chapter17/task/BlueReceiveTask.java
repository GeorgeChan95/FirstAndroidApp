package com.george.chapter17.task;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BlueReceiveTask extends Thread {

    private static final String TAG = "GeorgeTag";

    private Activity mAct; // 声明一个活动实例
    private BlueReceiveListener mListener; // 声明一个蓝牙接收的监听器对象
    private BluetoothSocket mSocket; // 声明一个蓝牙套接字对象

    public BlueReceiveTask(Activity act, BluetoothSocket socket, BlueReceiveListener listener) {
        mAct = act;
        mListener = listener;
        mSocket = socket;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;
        while (true) {
            try {
                // 从蓝牙Socket中读取输入流，并从中读取输入数据
                bytes = mSocket.getInputStream().read(buffer);
                // 将字节数据转成字符串
                String message = new String(buffer, 0, bytes);
                Log.d(TAG, "从客户端读取到消息：" + message);
                // 将读到的数据通过处理器送回给UI主线程处理
                mAct.runOnUiThread(() -> mListener.onBlueReceive(message));
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    /**
     * 定义一个蓝牙接收的监听器接口，在获得响应之后回调onBlueAccept方法
     */
    public interface BlueReceiveListener {
        void onBlueReceive(String message);
    }
}
