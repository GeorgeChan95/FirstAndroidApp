package com.george.chapter05;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class TimePickerActivity extends AppCompatActivity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {
    private TimePicker tpDate;
    private TextView tv_Result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);
        // 给选择按钮和确定按钮，绑定点击事件
        findViewById(R.id.btn_choose_time).setOnClickListener(this);
        findViewById(R.id.btn_enter).setOnClickListener(this);

        tpDate = findViewById(R.id.tp_date);
        tpDate.setIs24HourView(true);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
        int hour = calendar.get(Calendar.HOUR); // 12小时制的时间
        int hour24 = calendar.get(Calendar.HOUR_OF_DAY); // 24小时制时间
        tpDate.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        tv_Result = findViewById(R.id.tv_time);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_choose_time: // 通过TimePickerDialog组件，选择一个日期
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                // 最后一个参数为true，表示使用24小时制，false表示12小时制
                TimePickerDialog dialog = new TimePickerDialog(this, this, hour, minute, true);
                dialog.show();
                break;
            case R.id.btn_enter: // 通过datePicker组件获取日期
                int hour1 = tpDate.getCurrentHour();
                int minute1 = tpDate.getCurrentMinute();
                String desc = String.format("您选择的时间是%d时%d分", hour1, minute1);
                tv_Result.setText(desc);
                break;
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String desc = String.format("您选择的时间是%d时%d分", hourOfDay, minute);
        tv_Result.setText(desc);
    }
}