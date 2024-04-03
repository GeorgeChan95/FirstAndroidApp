package com.george.billdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.george.billdemo.adpater.BillPagerAdpater;
import com.george.billdemo.database.DBHelperUtil;
import com.george.util.DateUtil;

import java.util.Calendar;

public class BillPagerActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, ViewPager.OnPageChangeListener {

    private TextView tv_month; // 月份文本视图
    private ViewPager vp_bill; // 翻页视图组件
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_pager);

        // 设置标题和按钮
        TextView tv_title = findViewById(R.id.tv_title);
        TextView tv_option = findViewById(R.id.tv_option);
        tv_title.setText("账单列表");
        tv_option.setText("添加账单");
        // 标题行返回键添加点击事件
        findViewById(R.id.iv_back).setOnClickListener(this);
        // 添加账单按钮添加点击事件
        findViewById(R.id.tv_option).setOnClickListener(this);
        // 月份选择
        tv_month = findViewById(R.id.tv_month);
        tv_month.setOnClickListener(this);
        tv_month.setText(DateUtil.getMonth(calendar));
        // 翻页视图组件
        vp_bill = findViewById(R.id.vp_bill);
        // 初始化翻页视图
        initViewPager();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            // 账单列表页，返回直接关闭
            finish();
        } else if (v.getId() == R.id.tv_month) {
            // 点击月份选择框
            DatePickerDialog dialog = new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            DatePicker datePicker = dialog.getDatePicker();
            // 隐藏控件 日
            // 获取年月日的下拉列表项
            ViewGroup vg = (ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0);
            if (vg.getChildCount() == 3) {
                // 有的机型显示格式为“月日年”，此时隐藏第三个控件
                vg.getChildAt(1).setVisibility(View.GONE);
            } else if (vg.getChildCount() == 5) {
                // 有的机型显示格式为“月|日|年”，此时隐藏第2个和第3个控件（即“|日”）
                vg.getChildAt(1).setVisibility(View.GONE);
                vg.getChildAt(2).setVisibility(View.GONE);
            }
            dialog.show();
        } else if (v.getId() == R.id.tv_option) {
            Intent intent = new Intent(this, BillAddActivity.class);
            // 设置启动标志
            // https://mrfzh.github.io/2019/09/15/Activity-%E7%9A%84%E5%90%AF%E5%8A%A8%EF%BC%9A%E5%9B%9B%E7%A7%8D%E5%90%AF%E5%8A%A8%E6%A8%A1%E5%BC%8F%E5%8F%8A%E5%90%84%E7%A7%8D-FLAG/
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // 跳到账单填写页面
            startActivity(intent);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String monthText = DateUtil.getMonth(calendar);
        tv_month.setText(monthText);
        vp_bill.setCurrentItem(month);// 设置翻页视图显示第几页
    }

    /**
     * 初始化翻页视图
     */
    private void initViewPager() {
        // 从布局视图中获取名叫pts_bill的翻页标签栏
        PagerTabStrip pts_bill = findViewById(R.id.pts_bill);
        // 设置标签栏文本大小
        pts_bill.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        // 构建一个翻页适配器
        BillPagerAdpater adpater = new BillPagerAdpater(getSupportFragmentManager(), calendar.get(Calendar.YEAR));
        // 给ViewPager设置适配器
        vp_bill.setAdapter(adpater);
        vp_bill.setCurrentItem(calendar.get(Calendar.MONTH)); // 设置翻页视图显示第几页
        vp_bill.addOnPageChangeListener(this); // 给翻页视图添加页面变更监听器
    }

    /**
     * 在翻页过程中触发
     * @param position
     * @param positionOffset
     * @param positionOffsetPixels
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * 在翻页结束后触发
     * @param position
     */
    @Override
    public void onPageSelected(int position) {
        calendar.set(Calendar.MONTH, position);
        tv_month.setText(DateUtil.getMonth(calendar));
    }

    /**
     * 翻页状态改变时触发
     * @param state
     */
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}