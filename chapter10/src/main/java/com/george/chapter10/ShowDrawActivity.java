package com.george.chapter10;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.george.chapter10.listener.DrawSelectedListener;
import com.george.chapter10.widget.DrawRelativeLayout;

/**
 * 自定义绘图方式
 */
public class ShowDrawActivity extends AppCompatActivity {

    private TextView tv_draw;
    private Spinner sp_draw;
    public static String[] descArray = {"不画图", "画矩形", "画圆角矩形", "画圆圈", "画椭圆",
            "onDraw画叉叉", "dispatchDraw画叉叉"};
    public static int[] typeArray = {0, 1, 2, 3, 4, 5, 6};
    private Button btn_center;
    private DrawRelativeLayout drl_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_draw);

        tv_draw = findViewById(R.id.tv_draw);
        sp_draw = findViewById(R.id.sp_draw);

        // 自定义绘画区域
        drl_content = findViewById(R.id.drl_content);
        // 中间按钮控件
        btn_center = findViewById(R.id.btn_center);
        // 初始化绘图方式下拉框
        initTypeSpinner();
    }

    /**
     * 初始化绘图方式下拉框
     */
    private void initTypeSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_select, descArray);
        sp_draw.setPrompt("请选择绘图方式");
        sp_draw.setAdapter(adapter);
        sp_draw.setOnItemSelectedListener(new DrawSelectedListener(btn_center, drl_content));
    }
}