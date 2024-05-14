package com.george.chapter17.receiver;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import com.george.chapter17.BluetoothPairActivity;
import com.george.chapter17.util.BluetoothUtil;
import com.george.chapter17.util.BlutoothPariUtils;
import com.george.chapter17.util.ClsUtils;

import java.lang.reflect.Method;

@SuppressLint("MissingPermission")
public class BluetoothActivityReceiver extends BroadcastReceiver {
    private static final String TAG = "GeorgeTag";

    private BluetoothPairActivity mActivity;
    private TextView tv_discovery; // 声明一个文本视图对象
    private Handler mHandler = new Handler(Looper.myLooper()); // 声明一个处理器对象

    public BluetoothActivityReceiver() {
    }

    public BluetoothActivityReceiver(BluetoothPairActivity bluetoothActivity, TextView tv_discovery) {
        this.mActivity = bluetoothActivity;
        this.tv_discovery = tv_discovery;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "discoveryReceiver.onReceive: " + action);
        if (action.equals("android.bluetooth.device.action.PAIRING_REQUEST")) { // 蓝牙配对跳过输入配对码
            BluetoothDevice mBluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            Bundle extras = intent.getExtras();
            Object device = extras.get("android.bluetooth.device.extra.DEVICE");
            Object pairkey = extras.get("android.bluetooth.device.extra.PAIRING_KEY");
            Log.d(TAG, "device-->" + String.valueOf(device));
            Log.d(TAG, "pairkey-->" + String.valueOf(pairkey));

            try {
                //(三星)4.3版本测试手机还是会弹出用户交互页面(闪一下)，如果不注释掉下面这句页面不会取消但可以配对成功。(中兴，魅族4(Flyme 6))5.1版本手机两中情况下都正常
//                ClsUtils.setPairingConfirmation(mBluetoothDevice.getClass(), mBluetoothDevice, true);
//                abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
                //3.调用setPin方法进行配对...
//                boolean ret = ClsUtils.setPin(mBluetoothDevice.getClass(), mBluetoothDevice, String.valueOf(pairkey));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
