package com.george.chapter05;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.TimeZone;

public class DatePickerActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private TextView dateResult;
    private DatePicker dpDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);
        // 给选择按钮和确定按钮，绑定点击事件
        findViewById(R.id.btn_choose_date).setOnClickListener(this);
        findViewById(R.id.btn_enter).setOnClickListener(this);

        dateResult = findViewById(R.id.tv_date);
        dpDate = findViewById(R.id.dp_date);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_choose_date: // 通过DatePickerDialog组件，选择一个日期
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(this, this, year, month, day);
                dialog.show();
                break;
            case R.id.btn_enter: // 通过datePicker组件获取日期
                int year1 = dpDate.getYear();
                int month1 = dpDate.getMonth() + 1;
                int day1 = dpDate.getDayOfMonth();
                String desc = String.format("您选择的日期是%d年%d月%d日", year1, month1, day1);
                dateResult.setText(desc);
                break;
        }
    }

    /**
     * 使用DatePickerDialog弹框，点击：‘确定’ 按钮的回调，获取选择的日期值
     * @param view
     * @param year
     * @param month
     * @param dayOfMonth
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String desc = String.format("您选择的日期是%d年%d月%d日", year, month, dayOfMonth);
        dateResult.setText(desc);
    }
}