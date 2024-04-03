package com.george.chapter10.listener;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.george.chapter10.ShowDrawActivity;
import com.george.chapter10.widget.DrawRelativeLayout;

public class DrawSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
    private Button btn_center;
    private DrawRelativeLayout drl_content;
    public DrawSelectedListener(Button btn_center, DrawRelativeLayout drl_content) {
        this.btn_center = btn_center;
        this.drl_content = drl_content;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // 选择的绘图方式
        int type = ShowDrawActivity.typeArray[position];
        if (type == 5 || type == 6) {
            btn_center.setVisibility(View.VISIBLE);
        } else {
            btn_center.setVisibility(View.GONE);
        }
        // 设置绘图布局的绘制类型
        drl_content.setDrawType(type);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
