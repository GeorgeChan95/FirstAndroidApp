package com.george.chapter04.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static String getNowTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }
}
