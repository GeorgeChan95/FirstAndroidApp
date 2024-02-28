package com.george.shopping.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.george.shopping.entity.CartInfo;
import com.george.shopping.entity.GoodsInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * shopping 数据库帮助对象
 */
public class ShoppingDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "GeorgeTag";

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

    /**
     * 获取购物车商品总数量
     * @return
     */
    public int countCartInfo() {
        int count = 0;
        String sql = "SELECT SUM(count) FROM " + CART_TABLE_NAME;
        Cursor cursor = rDB.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        // 关闭数据库游标
        cursor.close();
        return count;
    }

    /**
     * 获取所有的商品信息
     * @return
     */
    public List<GoodsInfo> getAllGoodsInfo() {
        List<GoodsInfo> list = new ArrayList<>();
        String sql = "SELECT * FROM " + GOODS_TABLE_NAME;
        Cursor cursor = rDB.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String description = cursor.getString(2);
            float price = cursor.getFloat(3);
            String pic_path = cursor.getString(4);

            GoodsInfo goodsInfo = new GoodsInfo();
            goodsInfo.id = id;
            goodsInfo.name = name;
            goodsInfo.description = description;
            goodsInfo.price = price;
            goodsInfo.picPath = pic_path;

            list.add(goodsInfo);
        }
        // 关闭数据库游标
        cursor.close();
        return list;
    }

    /**
     * 向购物车表中添加记录
     * @param goodsId 商品id
     */
    public void insertCartInfo(int goodsId) {
        // 获取商品在购物车中的数量
        CartInfo cartInfo = queryCartInfoByGoodsId(goodsId);
        if (cartInfo == null) {
            cartInfo = new CartInfo();
            cartInfo.count = 0;
            cartInfo.goodsId = goodsId;
        }
        // 获取商品信息
        GoodsInfo goodsInfo = queryGoodsInfoById(goodsId);
        if (goodsInfo == null) {
            Log.d(TAG, "数据库异常，商品信息为空，表名：" + GOODS_TABLE_NAME + " _id: " + goodsId);
            return;
        }

        ContentValues values = new ContentValues();
        values.put("good_id", goodsId);
        values.put("count", ++cartInfo.count);

        long row = wDB.insert(CART_TABLE_NAME, null, values);
        if (row > 0) {
            Log.d(TAG, "购物车数据保存操作成功");
        }
    }

    /**
     * 根据商品ID获取购物车信息
     * @param goodsId 商品ID
     * @return
     */
    private CartInfo queryCartInfoByGoodsId(int goodsId) {
        CartInfo cartInfo = null;
        Cursor cursor = rDB.query(CART_TABLE_NAME, null, " good_id=?", new String[]{String.valueOf(goodsId)}, null, null, null, " 1");
        while (cursor.moveToNext()) {
            cartInfo = new CartInfo();
            cartInfo.id = cursor.getInt(0);
            cartInfo.goodsId = cursor.getInt(1);
            cartInfo.count = cursor.getInt(2);
        }
        cursor.close();
        return cartInfo;
    }

    /**
     * 根据商品id获取商品信息
     * @param goodsId 商品id
     * @return
     */
    public GoodsInfo queryGoodsInfoById(int goodsId) {
        GoodsInfo goodsInfo = null;
        Cursor cursor = rDB.query(GOODS_TABLE_NAME, null, " _id=?", new String[]{String.valueOf(goodsId)}, null, null, null, " 1");
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String description = cursor.getString(2);
            float price = cursor.getFloat(3);
            String pic_path = cursor.getString(4);

            goodsInfo = new GoodsInfo();
            goodsInfo.id = id;
            goodsInfo.name = name;
            goodsInfo.description = description;
            goodsInfo.price = price;
            goodsInfo.picPath = pic_path;
        }
        cursor.close();
        return goodsInfo;
    }

    /**
     * 获取所有购物车数据集合
     * @return
     */
    public List<CartInfo> getAllCartInfo() {
        List<CartInfo> cartInfos = new ArrayList<>();

        String sql = "SELECT * FROM " + CART_TABLE_NAME;
        Cursor cursor = rDB.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            CartInfo cartInfo = new CartInfo();
            cartInfo.id = cursor.getInt(0);
            cartInfo.goodsId = cursor.getInt(1);
            cartInfo.count = cursor.getInt(2);
            cartInfos.add(cartInfo);
        }
        // 关闭数据库游标
        cursor.close();
        return cartInfos;
    }

    public boolean deleteFromCartInfo(int cartInfoId) {
        int row = wDB.delete(CART_TABLE_NAME, "_id=?", new String[]{String.valueOf(cartInfoId)});
        if (row > 0) {
            Log.d(TAG, "从数据库删除购物车商品信息操作成功, _id="+cartInfoId);
            return true;
        }
        return false;
    }

    /**
     * 清空购物车表
     * @return
     */
    public boolean deleteAllCartInfo() {
        int row = wDB.delete(CART_TABLE_NAME, null, null);
        if (row > 0) {
            Log.d(TAG, "清空购物车表操作成功");
            return true;
        }
        return false;
    }
}
