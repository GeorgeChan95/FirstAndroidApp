package com.george.chapter11;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.george.chapter11.adapter.RecyclerLinearAdapter;
import com.george.chapter11.entity.NewsInfo;
import com.george.chapter11.widget.SpacesDecoration;

public class RecyclerLinearActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_linear);

        // 初始化线性布局的循环视图
        initRecyclerLinear();
    }

    /**
     * 初始化线性布局的循环视图
     */
    private void initRecyclerLinear() {
        // 从布局文件中获取名叫rv_linear的循环视图
        RecyclerView rv_linear = findViewById(R.id.rv_linear);
        // 创建一个垂直方向的线性布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        // 设置循环试图的布局管理器
        rv_linear.setLayoutManager(manager);
        // 构建适配器
        RecyclerLinearAdapter adapter = new RecyclerLinearAdapter(this, NewsInfo.getDefaultList());
        rv_linear.setAdapter(adapter);
        // 设置循环试图的动画效果
        rv_linear.setItemAnimator(new DefaultItemAnimator());
        // 设置视图的空白装饰，具体见：https://www.jianshu.com/p/bcbfb84fe6d1
        SpacesDecoration decoration = new SpacesDecoration(1);
        rv_linear.addItemDecoration(decoration);
    }
}