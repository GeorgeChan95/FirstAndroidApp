package com.george.logintest.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerifyUtils {

    /**
     * 校验手机号是否合法
     * @param phoneNumber
     * @return
     */
    public static boolean checkPhoneNum(String phoneNumber) {
        // 定义手机号的正则表达式
        String regex = "^1[3-9]\\d{9}$";

        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);

        // 创建匹配器对象
        Matcher matcher = pattern.matcher(phoneNumber);

        // 返回是否匹配成功
        return matcher.matches();
    }
}
