package com.george.billdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.george.billdemo.database.DBHelperUtil;
import com.george.billdemo.entity.BillInfo;
import com.george.billdemo.service.BillService;
import com.george.util.DateUtil;
import com.george.util.ToastUtil;
import com.george.util.ViewUtils;

import java.util.Calendar;

public class BillAddActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, RadioGroup.OnCheckedChangeListener {
    private static final String TAG = "GeorgeTag";
    private Calendar calendar = Calendar.getInstance(); // 获取日历实例，里面包含了当前的年月日
    // 日期下拉选择控件
    private TextView tv_date;
    private int mBillType = 1; // 收入、支出类型，0-收入 1-支出
    private EditText et_desc; // 事项说明
    private EditText et_amount; // 金额
    // 数据库管理对象
    private static DBHelperUtil dbHelper;
    private static BillService billService;
    private RadioButton rg_income;
    private RadioButton rg_expand;
    private long _id = -1; // 账单_id，有_id表示编辑


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_add);

        TextView tv_title = findViewById(R.id.tv_title);
        TextView tv_option = findViewById(R.id.tv_option);
        findViewById(R.id.iv_back).setOnClickListener(this);
        tv_title.setText("请填写账单");
        tv_option.setText("账单列表");
        tv_option.setOnClickListener(this);

        // 日期选择控件
        tv_date = findViewById(R.id.tv_date);
        String date = DateUtil.getDate(Calendar.getInstance());
        tv_date.setText(date);
        tv_date.setOnClickListener(this);

        // 收支类型，单选控件
        RadioGroup rg_type = findViewById(R.id.rg_type);
        // 设置选项变更监听
        rg_type.setOnCheckedChangeListener(this);
        // 收入选项
        rg_income = findViewById(R.id.rg_income);
        // 支出选项
        rg_expand = findViewById(R.id.rg_expand);

        // 事项说明
        et_desc = findViewById(R.id.et_desc);
        // 事项说明
        et_amount = findViewById(R.id.et_amount);

        // 保存按钮
        findViewById(R.id.btn_save).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dbHelper = DBHelperUtil.getInstance(this);
        billService = new BillService(dbHelper.openReadLink(), dbHelper.openWriteLink());
        _id = getIntent().getLongExtra("_id", -1);
        if (_id > 0) { // id有值，就展示数据库里的账单详情
            BillInfo billInfo = billService.queryById(_id);
            if (billInfo != null) {
                calendar = DateUtil.formatString(billInfo.date);
                if (billInfo.type == 0) {
                    rg_income.setChecked(true);
                } else {
                    rg_expand.setChecked(true);
                }
            }
            et_desc.setText(billInfo.desc);
            et_amount.setText(billInfo.amount+"");
        }
        tv_date.setText(DateUtil.getDate(calendar));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_option) {
            Intent intent = new Intent(this, BillPagerActivity.class);
            // 设置启动标志
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // 跳转到帐单列表页面
            startActivity(intent);
        } else if (v.getId() == R.id.iv_back) {
            finish(); // 关闭当前页面
        } else if (v.getId() == R.id.tv_date) {
            // 点击日期控件，弹出日期选择对话框
            DatePickerDialog dialog = new DatePickerDialog(this, this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        } else if (v.getId() == R.id.btn_save) { // 点击保存
            saveBillInfo();
        }
    }

    /**
     * 保存账单信息
     */
    private void saveBillInfo() {
        // 隐藏软键盘
        ViewUtils.hideOnInputMethod(this, et_desc);
        ViewUtils.hideOnInputMethod(this, et_amount);
        String date = tv_date.getText().toString();
        BillInfo billInfo = new BillInfo();
        billInfo.date = date;
        billInfo.month = calendar.get(Calendar.YEAR) + String.format("%02d", calendar.get(Calendar.MONTH) + 1);
        billInfo.type = mBillType;
        billInfo.amount = Double.valueOf(et_amount.getText().toString());
        billInfo.desc = et_desc.getText().toString();
        if (_id > 0) billInfo._id = _id;

        // 数据库保存
        boolean res = billService.save(billInfo);
        if (res) {
            ToastUtil.show(this, "保存成功");
            // 重置页面
            resetPage();
        } else {
            ToastUtil.show(this, "操作失败");
        }
    }

    /**
     * 重置页面
     */
    private void resetPage() {
        calendar = Calendar.getInstance();
        rg_expand.setChecked(true);
        et_desc.setText("");
        et_amount.setText("");
        tv_date.setText(DateUtil.getDate(calendar));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        tv_date.setText(DateUtil.getDate(calendar));
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        mBillType = (checkedId == R.id.rg_income) ? 0 : 1;
    }

    @Override
    protected void onStop() {
        super.onStop();
        dbHelper.closeDBLink();
    }
}