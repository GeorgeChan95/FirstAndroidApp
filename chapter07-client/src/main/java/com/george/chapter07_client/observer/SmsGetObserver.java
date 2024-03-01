package com.george.chapter07_client.observer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class SmsGetObserver extends ContentObserver {
    private String TAG = "GeorgeTag";

    private Context mContext;

    /**
     * Creates a content observer.
     */
    public SmsGetObserver(Context context) {
        super(new Handler(Looper.getMainLooper()));
        this.mContext = context;
    }

    @SuppressLint("Range")
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);

        // onChange会多次调用，收到一条短信会调用两次onChange
        // mUri===content://sms/raw/20
        // mUri===content://sms/inbox/20
        // 安卓7.0以上系统，点击标记为已读，也会调用一次
        // mUri===content://sms
        // 收到一条短信都是uri后面都会有确定的一个数字，对应数据库的_id，比如上面的20
        if (uri == null) {
            return;
        }

        if (uri.toString().contains("content://sms/raw") || uri.toString().equals("content://sms")) {
            Log.d(TAG, "拒绝的URI：" + uri.toString());
            return;
        }

        Log.d(TAG, "不拒绝的Uri是：" + uri.toString());

        Cursor cursor = mContext.getContentResolver().query(uri, new String[]{"address", "body", "date"}, null, null, "date");
        while (cursor.moveToNext()) {
            String address = cursor.getString(cursor.getColumnIndex("address"));
            String body = cursor.getString(cursor.getColumnIndex("body"));
            String date = cursor.getString(cursor.getColumnIndex("date"));

            Log.d(TAG, "接收到短信，address: + " + address + " ,内容：" + body + ", 时间：" + date);

        }
        cursor.close();
    }
}
