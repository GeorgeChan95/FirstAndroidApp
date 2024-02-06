package com.george.chapter05;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.TextView;

public class RadioHorizontalActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_horizontal);
        RadioGroup genderGroup = findViewById(R.id.rg_gender);
        genderGroup.setOnCheckedChangeListener(this);
        result = findViewById(R.id.tv_result);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        Log.d("性别TAG", checkedId+"");
        switch (checkedId) {
            case R.id.rb_male:
                result.setText("您的性别是：" + "男");
                break;
            case R.id.rb_female:
                result.setText("您的性别是：" + "女");
                break;
            default:
                break;
        }
    }
}