package com.george.chapter09.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.george.chapter09.BroadOrderActivity;

public class OrderAReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction().equals(BroadOrderActivity.ORDER_ACTION)) {
            Log.d(BroadOrderActivity.TAG, "OrderAReceiver 收到一个广播");
        }
    }
}