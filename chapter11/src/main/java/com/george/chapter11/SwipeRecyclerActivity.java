package com.george.chapter11;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.george.chapter11.adapter.LinearDynamicAdapter;
import com.george.chapter11.entity.NewsInfo;
import com.george.chapter11.listener.OnItemClickListener;
import com.george.chapter11.listener.OnItemDeleteClickListener;
import com.george.chapter11.listener.OnItemLongClickListener;

import java.util.List;
import java.util.Random;

public class SwipeRecyclerActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, OnItemClickListener, OnItemLongClickListener, OnItemDeleteClickListener {

    // 声明一个下拉刷新布局对象
    private SwipeRefreshLayout srl_dynamic;
    // 声明一个循环视图
    private RecyclerView rv_dynamic;
    private List<NewsInfo> mPublicList = NewsInfo.getDefaultList(); // 当前的公众号信息列表
    private List<NewsInfo> mOriginList = NewsInfo.getDefaultList(); // 原始的公众号信息列表
    // 声明一个线性布局适配器
    private LinearDynamicAdapter mAdapter;
    // 声明处理器对象
    private Handler handler = new Handler(Looper.myLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_recycler);

        srl_dynamic = findViewById(R.id.srl_dynamic);
        // 设置下拉布局的下拉刷新监听器
        srl_dynamic.setOnRefreshListener(this);
        // 设置下拉刷新布局的进度圆圈颜色
        srl_dynamic.setColorSchemeResources(R.color.red, R.color.orange, R.color.green, R.color.blue);

        // 初始化动态线性布局循环试图
        initRecyclerDynamic();
    }

    /**
     * 初始化动态线性布局循环试图
     */
    private void initRecyclerDynamic() {
        // 从布局文件中获取名叫rv_dynamic的循环视图
        rv_dynamic = findViewById(R.id.rv_dynamic);
        // 线性布局管理
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv_dynamic.setLayoutManager(manager);

        // 构建适配器
        mAdapter = new LinearDynamicAdapter(this, mPublicList);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
        mAdapter.setOnItemDeleteClickListener(this);
        rv_dynamic.setAdapter(mAdapter);

    }

    /**
     * 松开刷新控件时触发该方法
     */
    @Override
    public void onRefresh() {
        // 模拟网络延迟3秒后，刷新列表
        handler.postDelayed(runnable, 3000);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int position = new Random().nextInt(mOriginList.size()-1);
            NewsInfo old = mOriginList.get(position);
            // 在顶部添加一条公众号消息
            mPublicList.add(0, old);
            // 通知适配器列表在第一项插入数据
            mAdapter.notifyItemInserted(0);
            // 让循环视图滚动到第一项所在的位置
            rv_dynamic.scrollToPosition(0);
            // 当循环视图的列表项已经占满整个屏幕时，再往顶部添加一条新记录，
            // 感觉屏幕没有发生变化，也没看到插入动画。
            // 此时就要调用scrollToPosition(0)方法，表示滚动到第一条记录。

            // 结束下拉刷新动作
            srl_dynamic.setRefreshing(false);
        }
    };

    /**
     * 点击循环适配器的列表项，触发点击监听器的onItemClick方法
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(View view, int position) {
        String desc = String.format("您点击了第%d项，标题是%s", position + 1,
                mPublicList.get(position).title);
        Toast.makeText(this, desc, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长按循环适配器的列表项，触发长按监听器的onItemLongClick方法
     * @param view
     * @param position
     */
    @Override
    public void onItemLongClick(View view, int position) {
        NewsInfo item = mPublicList.get(position);
        item.isPressed = !item.isPressed;
        mPublicList.set(position, item);
        // 通知适配器指定位置的数据发生了变更
        mAdapter.notifyItemChanged(position);
    }

    /**
     * 点击循环适配器列表项的删除按钮，触发删除监听器的onItemDeleteClick方法
     * @param view
     * @param position
     */
    @Override
    public void onItemDeleteClick(View view, int position) {
        mPublicList.remove(position);
        // 通知适配器删除指定位置的数据
        mAdapter.notifyItemRemoved(position);
    }
}