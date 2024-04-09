package com.george.chapter11;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class TabButtonActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private CheckBox ck_tab;
    private TextView tv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_button);

        ck_tab = findViewById(R.id.ck_tab);
        ck_tab.setOnCheckedChangeListener(this);

        tv_result = findViewById(R.id.tv_result);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == ck_tab.getId()) {
            tv_result.setText("当前控件：" + (isChecked ? "已选中" : "未选中"));
        }
    }
}