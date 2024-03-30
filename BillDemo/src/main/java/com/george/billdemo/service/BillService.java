package com.george.billdemo.service;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.george.billdemo.entity.BillInfo;
import com.george.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BillService {
    private String TAG = "GeorgeTag";
    private static final String TABLE_NAME = "bill_info";
    // 数据库读连接
    private SQLiteDatabase rDB;
    // 数据库写连接
    private SQLiteDatabase wDB;

    public BillService(SQLiteDatabase rDB, SQLiteDatabase wDB) {
        this.rDB = rDB;
        this.wDB = wDB;
    }


    /**
     * 根据月份获取账单数据集
     * @param mMonth
     * @return
     */
    public List<BillInfo> queryByMonth(String mMonth) {
        List<BillInfo> list = new ArrayList<>();
        if (mMonth == null || mMonth.length() <= 0) return list;

        String sql = "SELECT _id, date, month, type, amount, desc, create_time, update_time FROM bill_info WHERE month = ? ORDER BY _id DESC";
        Cursor cursor = rDB.rawQuery(sql, new String[]{mMonth});
        while (cursor.moveToNext()) {
            BillInfo info = new BillInfo();
            info._id = cursor.getLong(0);
            info.date = cursor.getString(1);
            info.month = cursor.getString(2);
            info.type = cursor.getInt(3);
            info.amount = cursor.getDouble(4);
            info.desc = cursor.getString(5);
            info.create_time = cursor.getString(6);
            info.update_time = cursor.getString(7);
            list.add(info);
        }
        cursor.close();
        Log.d(TAG, "获取到账单数据：" + list.size() + "条");
        return list;
    }

    /**
     * 根据ID获取
     * @param id
     * @return
     */
    public BillInfo queryById(long id) {
        String sql = "SELECT _id, date, month, type, amount, desc, create_time, update_time FROM bill_info WHERE _id = ? LIMIT 1";
        Cursor cursor = rDB.rawQuery(sql, new String[]{String.valueOf(id)});
        BillInfo info = null;
        while (cursor.moveToNext()) {
            info = new BillInfo();
            info._id = cursor.getLong(0);
            info.date = cursor.getString(1);
            info.month = cursor.getString(2);
            info.type = cursor.getInt(3);
            info.amount = cursor.getDouble(4);
            info.desc = cursor.getString(5);
            info.create_time = cursor.getString(6);
            info.update_time = cursor.getString(7);
        }
        cursor.close();
        Log.d(TAG, String.format("获取到账单_id: %d 对应的数据：%s", id, info==null?"":info.toString()));
        return info;
    }

    /**
     * 保存账单信息
     * @param billInfo
     */
    public boolean save(BillInfo billInfo) {
        long row = 0;
        ContentValues values = new ContentValues();
        values.put("amount", billInfo.amount);
        values.put("date", billInfo.date);
        values.put("month", billInfo.month);
        values.put("type", billInfo.type);
        values.put("desc", billInfo.desc);
        if (billInfo._id != null && billInfo._id > 0) {
            values.put("_id", billInfo._id);
            values.put("update_time", DateUtil.getDate(Calendar.getInstance()));
            row = wDB.update(TABLE_NAME, values, " _id=?", new String[]{billInfo._id.toString()});
        } else {
            values.put("create_time", DateUtil.getDate(Calendar.getInstance()));
            row = wDB.insert(TABLE_NAME, null, values);
        }
        if (row > 0) return true;

        return false;
    }

    /**
     * 删除账单
     * @param id
     */
    public boolean deleteById(Long id) {
        int row = wDB.delete(TABLE_NAME, " _id=?", new String[]{id.toString()});
        return row > 0;
    }
}
