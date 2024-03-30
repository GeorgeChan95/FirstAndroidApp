package com.george.billdemo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.george.billdemo.R;
import com.george.billdemo.adpater.BillListAdapter;
import com.george.billdemo.database.DBHelperUtil;
import com.george.billdemo.entity.BillInfo;
import com.george.billdemo.service.BillService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class BillFragment extends Fragment {

    // 声明一个上下文对象
    private Context mContext;
    // 选择的月份
    private String mMonth;
    // 声明一个视图对象
    private View mView;
    // 列表视图对象
    private ListView lv_bill;
    // 数据库管理对象
    private static DBHelperUtil dbHelper;
    private List<BillInfo> billInfoList = new ArrayList<>();

    public static BillFragment newInstance(String month) {
        BillFragment fragment = new BillFragment();// 创建碎片实例
        Bundle bundle = new Bundle();
        bundle.putString("month", month);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        if (getArguments() != null) { // 获取携带的参数
            mMonth = getArguments().getString("month", "01");
        }
        // 根据布局文件fragment_bill.xml生成视图对象
        mView = inflater.inflate(R.layout.fragment_bill, container, false);
        // 从布局视图中获取名叫lv_bill的列表视图
        lv_bill = mView.findViewById(R.id.lv_bill);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        dbHelper = DBHelperUtil.getInstance(mContext);
        BillService billService = new BillService(dbHelper.openReadLink(), dbHelper.openWriteLink());
        billInfoList = billService.queryByMonth(mMonth);
        if (billInfoList.size() > 0) {
            double income=0, expend=0;
            for (BillInfo bill : billInfoList) {
                if (bill.type == 0) {
                    // 收入金额累加
                    income += bill.amount;
                } else {
                    // 支出金额累加
                    expend += bill.amount;
                }
            }
            BillInfo sum = new BillInfo();
            sum.date = "合计";
            String incomeStr = BigDecimal.valueOf(income).setScale(2, RoundingMode.HALF_UP).toString();
            String expendStr = BigDecimal.valueOf(expend).setScale(2, RoundingMode.HALF_UP).toString();
            String balance = BigDecimal.valueOf(income-expend).setScale(2, RoundingMode.HALF_UP).toString();
            sum.desc = String.format("收入：%s元，\n支出：%s元", incomeStr, expendStr);
            sum.remark = String.format("余额：%s元", balance);
            billInfoList.add(sum);
        } else {
            BillInfo sum = new BillInfo();
            sum.date = "合计";
            sum.desc = String.format("收入：%s元\n支出：%s元", "0.00", "0.00");
            sum.remark = String.format("余额：%s元", "0.00");
            billInfoList.add(sum);
        }
        // 构建一个当月账单的列表适配器
        BillListAdapter listAdapter = new BillListAdapter(billInfoList, mContext);
        lv_bill.setAdapter(listAdapter);// 设置列表视图的适配器
        lv_bill.setOnItemClickListener(listAdapter); // 列表视图元素添加点击监听
        lv_bill.setOnItemLongClickListener(listAdapter); // 列表视图元素添加长按监听
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
