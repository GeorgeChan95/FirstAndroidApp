package com.george.billdemo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelperUtil extends SQLiteOpenHelper {
    private static final String DB_NAME = "bill.db";
    private static final String TABLE_NAME = "bill_info";
    private static final int DB_VERSION = 1;
    private static DBHelperUtil dbHelper;
    // 数据库读连接
    private SQLiteDatabase rDB;
    // 数据库写连接
    private SQLiteDatabase wDB;
    private Context mContext; // 上下文
    private String TAG = "GeorgeTag";

    public DBHelperUtil(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
    }

    /**
     * 懒汉式加载DBHelper
     * @param context
     * @return
     */
    public static DBHelperUtil getInstance(Context context) {
        if (dbHelper == null) {
            synchronized (DBHelperUtil.class) {
                if (dbHelper == null) {
                    dbHelper = new DBHelperUtil(context);
                }
            }
        }
        return dbHelper;
    }

    /**
     * 打开数据库读连接
     * @return
     */
    public SQLiteDatabase openReadLink() {
        if (rDB == null || !rDB.isOpen()) {
            rDB = dbHelper.getReadableDatabase();
        }
        return rDB;
    }

    /**
     * 获取数据库写链接
     * @return
     */
    public SQLiteDatabase openWriteLink() {
        if (wDB == null || !wDB.isOpen()) {
            wDB = dbHelper.getWritableDatabase();
        }
        return wDB;
    }

    /**
     * 关闭数据库读链接与写链接
     */
    public void closeDBLink() {
        if (rDB != null && rDB.isOpen()) {
            rDB.close();
        }
        if (wDB != null && wDB.isOpen()) {
            wDB.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql1 = "DROP TABLE IF EXISTS "+ TABLE_NAME + ";";
        String sql2 = "CREATE TABLE IF NOT EXISTS bill_info ( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "date VARCHAR NOT NULL, " +
                "month VARCHAR NOT NULL, " +
                "type INTEGER NOT NULL, " +
                "amount DOUBLE NOT NULL, " +
                "`desc` VARCHAR NOT NULL, " +
                "create_time VARCHAR NOT NULL, " +
                "update_time VARCHAR NULL " +
                ");";
        db.execSQL(sql1);
        db.execSQL(sql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade oldVersion=" + oldVersion + ", newVersion=" + newVersion);
        if (newVersion > 1) {
            //Android的ALTER命令不支持一次添加多列，只能分多次添加
            Log.d(TAG, "执行了onUpgrade方法");
        }
    }
}
