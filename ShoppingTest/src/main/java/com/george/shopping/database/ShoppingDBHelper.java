package com.george.shopping.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.george.shopping.entity.GoodsInfo;

import java.util.ArrayList;

/**
 * shopping 数据库帮助对象
 */
public class ShoppingDBHelper extends SQLiteOpenHelper {

    // 数据库帮助对象实例
    private static ShoppingDBHelper dbHelper;
    // 数据库名称
    private static final String DB_NAME = "shopping.db";
    // 数据库版本
    private static final Integer DB_VERSION = 1;
    private String GOODS_TABLE_NAME = "goods_info";
    private String CART_TABLE_NAME = "cart_info";
    // 数据库读连接对象
    private SQLiteDatabase rDB;
    // 数据库写连接对象
    private SQLiteDatabase wDB;

    /**
     * 默认必须实现的构造函数
     * @param context
     */
    public ShoppingDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * 单例模式获取数据库帮助对象，并返回
     * @param context 上下文对象
     * @return
     */
    public static ShoppingDBHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new ShoppingDBHelper(context);
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
     * 打开数据库写连接
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
    public void closeDatabaseLink() {
        if (rDB != null && rDB.isOpen()) {
            rDB.close();
            rDB = null;
        }
        if (wDB != null && wDB.isOpen()) {
            wDB.close();
            wDB = null;
        }
    }

    /**
     * 应用初始化时调用onCreate()方法
     * 创建数据库，执行建表语句
     *
     * @param db 数据库执行器
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建商品信息表
        String goodsSql = "CREATE TABLE IF NOT EXISTS "+ GOODS_TABLE_NAME + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "name VARCHAR NOT NULL," +
                "description VARCHAR NOT NULL," +
                "price FLOAT NOT NULL," +
                "pic_path VARCHAR " +
                ")";
        db.execSQL(goodsSql);
        // 创建购物车信息表
        String cartSql = "CREATE TABLE IF NOT EXISTS " + CART_TABLE_NAME + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "good_id INTEGER NOT NULL," +
                "count INTEGER NOT NULL DEFAULT 0" +
                ")";
        db.execSQL(cartSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 批量保存商品信息到数据库表
     * @param goodsInfos
     */
    public void insertGoodsInfos(ArrayList<GoodsInfo> goodsInfos) {
        // 插入多条记录，要么全部成功，要么全部失败
        try {
            wDB.beginTransaction();
            for (GoodsInfo info : goodsInfos) {
                ContentValues values = new ContentValues();
                values.put("name", info.name);
                values.put("description", info.description);
                values.put("price", info.price);
                values.put("pic_path", info.picPath);
                wDB.insert(GOODS_TABLE_NAME, null, values);
            }
            wDB.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            wDB.endTransaction();
        }
    }
}
