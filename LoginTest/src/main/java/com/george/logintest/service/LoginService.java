package com.george.logintest.service;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.george.logintest.entity.LoginInfo;

import java.util.Arrays;

public class LoginService {
    private String TAG = "GeorgeTag";
    private static final String LOGIN_TABLE = "login_info";
    // 数据库读连接
    private SQLiteDatabase rDB;
    // 数据库写连接
    private SQLiteDatabase wDB;

    public LoginService(SQLiteDatabase rDB, SQLiteDatabase wDB) {
        this.rDB = rDB;
        this.wDB = wDB;
    }

    /**
     * 保存用户登录信息
     * @param loginInfo
     * @return
     */
    public boolean saveLogin(LoginInfo loginInfo) {
        boolean flag = false;
        try {
            // 开启事务
            wDB.beginTransaction();

            // 先删除登录信息
            delete(loginInfo.getPhone());
            // 再保存登录信息
            insert(loginInfo);
            // 设置事务成功标记
            wDB.setTransactionSuccessful();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 结束事务
            wDB.endTransaction();
        }
        return flag;
    }

    /**
     * 保存用户登录信息
     * @param loginInfo
     * @return
     */
    public long insert(LoginInfo loginInfo) {
        ContentValues values = new ContentValues();
        values.put("phone", loginInfo.getPhone());
        values.put("password", loginInfo.getPassword());
        values.put("remember", loginInfo.getRemember());
        long row = wDB.insert(LOGIN_TABLE, null, values);
        if (row > 0) {
            Log.d(TAG, "保存登录信息操作成功");
        }
        return row;
    }

    /**
     * 根据手机号删除登录信息
     * @param phone 登录手机号
     * @return
     */
    public int delete(String phone) {
        int row = wDB.delete(LOGIN_TABLE, "phone=?", new String[]{phone});
        if (row>0) {
            Log.d(TAG, "删除用户登录信息操作成功，参数：" + phone);
        }
        return row;
    }

    /**
     * 获取最后一登录的，用户登录信息
     * @return
     */
    public LoginInfo queryLastLogin() {
        LoginInfo loginInfo = null;
        // 查询最后一个登录，且记住密码的数据
        String sql = "SELECT * FROM login_info WHERE remember = 1 ORDER BY id DESC LIMIT 1;";
        Cursor cursor = rDB.rawQuery(sql, null, null);
        if (cursor.moveToNext()) {
            String phone = cursor.getString(1);
            String password = cursor.getString(2);
            Integer remember = cursor.getInt(3);
            loginInfo = new LoginInfo(phone, password, remember);
        }
        return loginInfo;
    }

    /**
     * 根据手机号查询登录信息
     * @param phone 手机号
     * @return
     */
    public LoginInfo queryByPhone(String phone) {
        LoginInfo loginInfo = null;
        // 查询最后一个登录，且记住密码的数据
        String sql = "SELECT * FROM login_info WHERE remember = 1 and phone = ? ORDER BY id DESC LIMIT 1;";
        Cursor cursor = rDB.rawQuery(sql, new String[]{phone}, null);
        if (cursor.moveToNext()) {
            String password = cursor.getString(2);
            Integer remember = cursor.getInt(3);
            loginInfo = new LoginInfo(phone, password, remember);
        }
        return loginInfo;
    }
}
