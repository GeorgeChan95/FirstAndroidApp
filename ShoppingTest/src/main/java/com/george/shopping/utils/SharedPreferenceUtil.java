package com.george.shopping.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtil {
     private static SharedPreferenceUtil preferenceUtil;
     private SharedPreferences shoppingPreferences;

    /**
     * 单例模式获取 SharedPreferenceUtil 实例
     * 创建 shopping.xml 文件， 文件路径为：/data/data/应用包名/shared_prefs/文件名.xml
     * @param ctx
     * @return
     */
     public static SharedPreferenceUtil getInstance(Context ctx) {
         if (preferenceUtil == null) {
             preferenceUtil = new SharedPreferenceUtil();
             // 从shopping.xml获取共享参数实例,
             preferenceUtil.shoppingPreferences = ctx.getSharedPreferences("shopping", Context.MODE_PRIVATE);
         }
         return preferenceUtil;
     }

    /**
     * 设置购物应用的首次打开标记
     * @param flagKey 判断的key
     * @param value 值
     */
     public void setShoppingFirstFlag(String flagKey, boolean value) {
         SharedPreferences.Editor edit = shoppingPreferences.edit();
         edit.putBoolean(flagKey, value);
         edit.commit();
     }

    /**
     * 获取是否首次打开购物应用标记
     * @param flagKey 判断的key
     * @param defaultValue 默认值
     * @return
     */
     public boolean getShoppingFirstFlag(String flagKey, boolean defaultValue) {
         return shoppingPreferences.getBoolean(flagKey, defaultValue);
     }

}
