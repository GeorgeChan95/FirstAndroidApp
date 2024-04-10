package com.george.chapter11;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.george.chapter11.adapter.RecyclerGridAdapter;
import com.george.chapter11.entity.NewsInfo;
import com.george.chapter11.widget.SpacesDecoration;

public class RecyclerGridActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_grid);

        // 初始化网格布局循环试图
        initRecyclerGrid();
    }

    /**
     * 初始化网格布局循环试图s
     */
    private void initRecyclerGrid() {
        RecyclerView rv_grid = findViewById(R.id.rv_grid);
        GridLayoutManager manager = new GridLayoutManager(this, 5);
        rv_grid.setLayoutManager(manager);

        // 构建网格适配器
        RecyclerGridAdapter adapter = new RecyclerGridAdapter(this, NewsInfo.getDefaultGrid());
        // 设置点击监听器接口，由于该适配器已经实现了定义的接口，因此可以作为参数设置进去给接口参数赋值
        adapter.setClickListener(adapter);
        adapter.setLongClickListener(adapter);
        // 给视图添加适配器
        rv_grid.setAdapter(adapter);

        // 设置循环视图动画效果
        rv_grid.setItemAnimator(new DefaultItemAnimator());
        // 设置空白装饰
        SpacesDecoration decoration = new SpacesDecoration(this,1, R.drawable.drawable_divider);
        rv_grid.addItemDecoration(decoration);
    }
}