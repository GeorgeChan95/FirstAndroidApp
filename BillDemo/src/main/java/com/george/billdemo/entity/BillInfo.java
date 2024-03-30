package com.george.billdemo.entity;

public class BillInfo {
    // 数据id，自增
    public Long _id;
    // 日期，格式：YYYY-MM-DD
    public String date;
    // 月份，格式：YYYYMM
    public String month;
    // 账单类型：0-收入  1-支出
    public int type;
    // 金额
    public double amount;
    // 金额描述
    public String desc;
    public String create_time;
    public String update_time;
    public String remark;

    public BillInfo() {
        _id = 0L;
        date = "";
        month = "";
        type = 0;
        amount = 0.0;
        desc = "";
        create_time = "";
        update_time = "";
        remark = "";
    }


}
