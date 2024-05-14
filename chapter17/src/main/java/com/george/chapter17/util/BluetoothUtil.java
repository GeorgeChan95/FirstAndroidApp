package com.george.chapter17.util;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BluetoothUtil {
    private final static String TAG = "GeorgeTag";

    /**
     * 获取蓝牙的开关状态
     * @return
     */
    public static boolean getBlueToothStatus() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean enabled;
        switch (bluetoothAdapter.getState()) {
            case BluetoothAdapter.STATE_ON:
            case BluetoothAdapter.STATE_TURNING_ON:
                enabled = true;
                break;
            case BluetoothAdapter.STATE_OFF:
            case BluetoothAdapter.STATE_TURNING_OFF:
            default:
                enabled = false;
                break;
        }
        return enabled;
    }

    /**
     * 打开或关闭蓝牙
     * @param status
     */
    @SuppressLint("MissingPermission")
    public static void setBlueToothStatus(boolean status) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (status) {
            // 打开蓝牙
            adapter.enable();
        } else {
            // 关闭蓝牙
            adapter.disable();
        }
    }

    /**
     * 建立蓝牙配对
     * 蓝牙配对需要使用反射完成
     * @param device
     */
    public static boolean createBond(BluetoothDevice device) {
        boolean result = false;
        try {
            Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
            Log.d(TAG, "开始配对");
            result = (Boolean) createBondMethod.invoke(device);
            Log.d(TAG, "配对结果：" + result);
        } catch (NoSuchMethodException e) {
            result = false;
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            result = false;
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 移除已配对的蓝牙设备
     * 移除配对需要使用反射完成
     * @param device
     * @return
     */
    public static boolean removeBond(BluetoothDevice device) {
        boolean result = false;
        try {
            Method removeBondMethod = BluetoothDevice.class.getMethod("removeBond");
            Log.d(TAG, "取消配对");
            result = (boolean) removeBondMethod.invoke(device);
            Log.d(TAG, "取消配对操作结果：" + result);
        } catch (NoSuchMethodException e) {
            result = false;
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            result = false;
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 创建一个默认频道的监听
     * @param adapter
     * @return
     */
    public static BluetoothServerSocket listenServer(BluetoothAdapter adapter) {
        BluetoothServerSocket serverSocket = null;
        try {
            Method listenMethod = adapter.getClass().getMethod("listenUsingRfcommOn", int.class);
            serverSocket = (BluetoothServerSocket) listenMethod.invoke(adapter, 29);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serverSocket;
    }

    /**
     * 向对方设备发送信息
     * @param socket
     * @param message
     */
    public static void writeOutputStream(BluetoothSocket socket, String message) {
        Log.d(TAG, "begin writeOutputStream message=" + message);
        try {
            OutputStream os = socket.getOutputStream(); // 获得输出流对象
            os.write(message.getBytes()); // 往输出流写入字节形式的数据
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "end writeOutputStream");
    }
}
