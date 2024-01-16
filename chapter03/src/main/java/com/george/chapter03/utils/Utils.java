package com.george.chapter03.utils;

import android.content.Context;

public class Utils {

    public static int dip2px(Context context, float dpValue) {
        // 获取当前手机的像素密度（1dp对应几个px）
        float scale = context.getResources().getDisplayMetrics().density;

        return (int) (dpValue * scale + 0.5f); // 加0.5，是为了int在取整时进行四舍五入。
    }
}
