package com.george.chapter11;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.george.chapter11.util.ToastUtil;

/**
 * 顶部导航栏
 */
public class ToolbarActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar);
        // 获取ToolBar组件
        Toolbar tl_head = findViewById(R.id.tl_head);
        // 使用ToolBar替代系统自带的ActionBar，（需要给当前视图文件设置NoActionBar主题）
        setSupportActionBar(tl_head);
        tl_head.setLogo(R.drawable.ic_app);
        // 设置标题
        tl_head.setTitle("工具栏页面");
        tl_head.setTitleTextColor(Color.RED);
        // 设置副标题
        tl_head.setSubtitle("Toolbar");
        tl_head.setSubtitleTextColor(Color.GRAY);
        // 设置工具栏背景
        tl_head.setBackgroundResource(R.color.blue_light);
        // 设置工具栏左边的导航图表
        tl_head.setNavigationIcon(R.drawable.ic_back);
        // 给tl_head设置导航图标的点击监听器
        // setNavigationOnClickListener必须放到setSupportActionBar之后，不然不起作用
        tl_head.setNavigationOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        ToastUtil.show(this, "你点击了导航图标");
    }
}