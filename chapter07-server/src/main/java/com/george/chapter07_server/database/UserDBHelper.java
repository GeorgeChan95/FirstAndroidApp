package com.george.chapter07_server.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDBHelper extends SQLiteOpenHelper {
    private static int DB_VERSION = 1;
    public static final String TABLE_NAME = "user_info";
    private static UserDBHelper dbHelper;
    // 数据库读连接
    private SQLiteDatabase rDB;
    // 数据库写连接
    private SQLiteDatabase wDB;

    public UserDBHelper(Context context) {
        super(context, TABLE_NAME, null, DB_VERSION);
    }

    /**
     * 单例模式获取DBHelper
     * @param context
     * @return
     */
    public static UserDBHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new UserDBHelper(context);
        }
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " name VARCHAR NOT NULL," +
                " age INTEGER NOT NULL," +
                " height LONG NOT NULL," +
                " weight FLOAT NOT NULL," +
                " married INTEGER NOT NULL);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 获取数据库读连接
     * @return
     */
    public SQLiteDatabase openReadLink() {
        if (rDB == null || !rDB.isOpen()) {
            rDB = dbHelper.getReadableDatabase();
        }
        return rDB;
    }

    /**
     * 获取数据库写连接
     * @return
     */
    public SQLiteDatabase openWriteLink() {
        if (wDB == null || !wDB.isOpen()) {
            wDB = dbHelper.getWritableDatabase();
        }
        return wDB;
    }

    /**
     * 关闭数据库连接
     */
    public void closeLink() {
        if (rDB != null && rDB.isOpen()) {
            rDB.close();
        }
        if (wDB != null && wDB.isOpen()) {
            wDB.close();
        }
    }
}
