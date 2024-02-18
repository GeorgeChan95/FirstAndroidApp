package com.george.chapter06.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SQLiteDBHelper 公共方法
 */
public class DBHelperUtil extends SQLiteOpenHelper {
    private static final String DB_NAME = "user.db";
    private static final String USER_TABLE = "user_info";
    private static final int DB_VERSION = 2;
    // SQLiteOpenHelper实例
    private static DBHelperUtil dbHelper;
    // 数据库读连接
    private SQLiteDatabase rDB;
    // 数据库写连接
    private SQLiteDatabase wDB;
    private String TAG = "GeorgeTag";

    /**
     * 默认需要一个构造器，重写父类的构造方法
     * @param context
     */
    public DBHelperUtil(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * 单例模式获取 DBHelper实例
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

    /**
     * onCreate方法只在第一次打开数据库时执行, 在此可以创建表结构
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "执行了 onCreate 方法");
        String sql1 = "DROP TABLE IF EXISTS "+ USER_TABLE +";";
        String sql2 = "CREATE TABLE IF NOT EXISTS "+ USER_TABLE +" ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "name VARCHAR NOT NULL, " +
                "age INTEGER NOT NULL, " +
                "height LONG NOT NULL, " +
                "weight FLOAT NOT NULL, " +
                "married INTEGER NOT NULL);";
        db.execSQL(sql1);
        db.execSQL(sql2);
    }

    /**
     * 在数据库版本升高时执行，在此可以根据新旧版本号变更表结构。
     * 经测试: 当修改了 'DB_VERSION' 的值为2后，重启应用会自动调用 onUpgrade 方法
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade oldVersion=" + oldVersion + ", newVersion=" + newVersion);
        if (newVersion > 1) {
            //Android的ALTER命令不支持一次添加多列，只能分多次添加
            String alter_sql = "ALTER TABLE " + USER_TABLE + " ADD COLUMN phone VARCHAR;";
            Log.d(TAG, "alter_sql:" + alter_sql);
            db.execSQL(alter_sql);
            alter_sql = "ALTER TABLE " + USER_TABLE + " ADD COLUMN " + "password VARCHAR;";
            Log.d(TAG, "alter_sql:" + alter_sql);
            db.execSQL(alter_sql); // 执行完整的SQL语句
        }
    }
}
