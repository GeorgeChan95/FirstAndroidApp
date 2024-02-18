package com.george.chapter06.service;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.george.chapter06.entity.UserInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserService {
    private String TAG = "GeorgeTag";
    private static final String USER_TABLE = "user_info";
    // 数据库读连接
    private SQLiteDatabase rDB;
    // 数据库写连接
    private SQLiteDatabase wDB;

    public UserService(SQLiteDatabase rDB, SQLiteDatabase wDB) {
        this.rDB = rDB;
        this.wDB = wDB;
    }

    public long insert(UserInfo userInfo) {
        ContentValues values = new ContentValues();
        values.put("name", userInfo.getName());
        values.put("age", userInfo.getAge() == null ? 0 : userInfo.getAge());
        values.put("height", userInfo.getHeight() == null ? 0l : userInfo.getHeight());
        values.put("weight", userInfo.getWeight() == null ? 0f : userInfo.getWeight());
        values.put("married", userInfo.getMarried() ? 1 : 0);
        long row = wDB.insert(USER_TABLE, null, values);
        if (row > 0) {
            Log.d(TAG, "UserService insert 操作成功， 数据：" + values.toString());
        }
        return row;
    }

    /**
     * 数据更新
     * @param userInfo 数据对象
     * @param condition 条件语句
     * @param params 参数数组
     * @return
     */
    public int update(UserInfo userInfo, String condition, String[] params) {
        ContentValues values = new ContentValues();
        values.put("name", userInfo.getName());
        values.put("age", userInfo.getAge() == null ? 0 : userInfo.getAge());
        values.put("height", userInfo.getHeight() == null ? 0l : userInfo.getHeight());
        values.put("weight", userInfo.getWeight() == null ? 0f : userInfo.getWeight());
        values.put("married", userInfo.getMarried() ? 1 : 0);
        int row = wDB.update(USER_TABLE, values, condition, params);
        if (row > 0) {
            Log.d(TAG, "UserService update 操作成功，条件语句：" + condition + "\t参数：" + Arrays.toString(params));
        }
        return row;
    }

    /**
     * 根据条件删除表记录
     * @param condition 条件语句
     * @param params 参数数组
     * @retur4
     */
    public int delete(String condition, String[] params) {
        int row = wDB.delete(USER_TABLE, condition, params);
        if (row > 0) {
            Log.d(TAG, "UserService delete 操作成功，条件语句：" + condition + "\t参数：" + Arrays.toString(params));
        }
        return row;
    }

    /**
     * 查询数据
     * @param columns 要查询的列，null表示查询所有列
     * @param condition 查询条件语句
     * @param params 查询参数
     * @param groupBy 格式为SQL group BY子句（不包括group BY本身）。传递null将导致行不分组。
     * @param having 格式为SQL having子句（不包括having本身）。传递null将导致包括所有行组，并且在不使用行分组时是必需的。
     * @param orderBy 对行进行排序，格式为SQL order BY子句（不包括order BY本身）。传递null将使用默认的排序顺序，该排序顺序可能是无序的。
     * @return
     */
    public List<UserInfo> query(String[] columns, String condition, String[] params, String groupBy, String having, String orderBy) {
        List<UserInfo> userList = new ArrayList<>();
        // 执行记录查询动作，该语句返回结果集的游标
        Cursor cursor = rDB.query(USER_TABLE, columns, condition, params, groupBy, having, orderBy);
        // 循环取出游标指向的每条记录
        if (cursor.moveToNext()) {
            UserInfo userInfo = new UserInfo();
            userInfo.setId(cursor.getInt(0));
            userInfo.setName(cursor.getString(1));
            userInfo.setAge(cursor.getInt(2));
            userInfo.setHeight(cursor.getLong(3));
            userInfo.setWeight(cursor.getFloat(4));
            userInfo.setMarried(cursor.getInt(5) > 0 ? true : false);
            userList.add(userInfo);
        }
        return userList;
    }
}
