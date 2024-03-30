package com.george.billdemo.adpater;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.george.billdemo.BillAddActivity;
import com.george.billdemo.R;
import com.george.billdemo.database.DBHelperUtil;
import com.george.billdemo.entity.BillInfo;
import com.george.billdemo.service.BillService;
import com.george.util.ToastUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class BillListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private static final String TAG = "GeorgeTag";
    private List<BillInfo> mBillList;
    private Context mContext;

    public BillListAdapter(List<BillInfo> billList, Context context) {
        this.mBillList = billList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mBillList.size();
    }

    @Override
    public Object getItem(int position) {
        return mBillList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            // 根据布局文件item_bill.xml生成转换视图对象
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_bill, null);
            holder.tv_date = convertView.findViewById(R.id.tv_date);
            holder.tv_desc = convertView.findViewById(R.id.tv_desc);
            holder.tv_amount = convertView.findViewById(R.id.tv_amount);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BillInfo bill = mBillList.get(position);
        holder.tv_date.setText(bill.date);
        holder.tv_desc.setText(bill.desc);
        if (bill.date.equals("合计")) {
            holder.tv_amount.setText(bill.remark);
        } else {
            String amount = BigDecimal.valueOf(bill.amount).setScale(2, RoundingMode.HALF_UP).toString();
            holder.tv_amount.setText(String.format("%s%s元", bill.type==0?"收入":"支出", amount));
        }
        return convertView;
    }

    /**
     * 列表项的点击监听
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BillInfo billInfo = mBillList.get(position);
        // 合计行不响应点击事件
        if (billInfo.date != null && billInfo.date.equals("合计")) return;
        // 跳转账单保存页面，编辑该账单
        Intent intent = new Intent(mContext, BillAddActivity.class);
        // 将账单_id作为参数传递
        intent.putExtra("_id", billInfo._id);
        // 跳转账单编辑页，因为已存在该账单，所以跳过去实际会编辑账单
        mContext.startActivity(intent);
    }

    /**
     * 列表项的长按监听
     * @param parent
     * @param view
     * @param position
     * @param id
     * @return
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        BillInfo billInfo = mBillList.get(position);
        // 合计行不响应长按事件
        if (billInfo.date != null && billInfo.date.equals("合计")) return false;

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("确认删除该行数据？");
        builder.setTitle("删除");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 删除账单
                deleteBill(position);
            }
        });

        builder.setNegativeButton("取消", null);
        builder.create().show();
        return true;
    }

    /**
     * 删除账单
     * @param position
     */
    private void deleteBill(int position) {
        BillInfo billInfo = mBillList.get(position);
        mBillList.remove(position);
        // 重新计算合计数据
        recountSum(mBillList);

        // 通知适配器发生了数据变化
        notifyDataSetChanged();

        // 删除数据
        DBHelperUtil dbHelper = DBHelperUtil.getInstance(mContext);
        BillService billService = new BillService(dbHelper.openReadLink(), dbHelper.openWriteLink());
        boolean res = billService.deleteById(billInfo._id);
        if (res) {
            ToastUtil.show(mContext, "删除成功");
        } else {
            ToastUtil.show(mContext, "操作失败");
        }
    }

    /**
     * 重新计算合计数据
     * @param mBillList
     */
    private void recountSum(List<BillInfo> mBillList) {
        BillInfo billInfo = mBillList.get(mBillList.size() - 1);
        if (mBillList.size() == 1) {
            billInfo.date = "合计";
            billInfo.desc = String.format("收入：%s元\n支出：%s元", "0.00", "0.00");
            billInfo.remark = String.format("余额：%s元", "0.00");
        } else {
            double income=0, expend=0;
            for (BillInfo bill : mBillList) {
                if (bill.type == 0) {
                    // 收入金额累加
                    income += bill.amount;
                } else {
                    // 支出金额累加
                    expend += bill.amount;
                }
            }
            billInfo.date = "合计";
            String incomeStr = BigDecimal.valueOf(income).setScale(2, RoundingMode.HALF_UP).toString();
            String expendStr = BigDecimal.valueOf(expend).setScale(2, RoundingMode.HALF_UP).toString();
            String balance = BigDecimal.valueOf(income-expend).setScale(2, RoundingMode.HALF_UP).toString();
            billInfo.desc = String.format("收入：%s元，\n支出：%s元", incomeStr, expendStr);
            billInfo.remark = String.format("余额：%s元", balance);
        }
    }

    public final class ViewHolder {
        public TextView tv_date;
        public TextView tv_desc;
        public TextView tv_amount;
    }
}
