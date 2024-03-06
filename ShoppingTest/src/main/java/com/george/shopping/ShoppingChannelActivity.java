package com.george.shopping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.george.shopping.adapter.GoodsAdapter;
import com.george.shopping.database.ShoppingDBHelper;
import com.george.shopping.entity.GoodsInfo;
import com.george.shopping.interfaces.AddCartListener;
import com.george.shopping.utils.ToastUtil;

import java.util.List;

public class ShoppingChannelActivity extends AppCompatActivity implements View.OnClickListener, AddCartListener {
    private final String TAG = "GeorgeTag";
    // 头部标题
    private TextView tv_title;
    // 表格布局容器
    private GridView gv_channel;
    // 购物车商品总数量
    private TextView tv_count;
    // 商品数据库帮助对象
    private ShoppingDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "ShoppingChannelActivity 调用onCreate方法");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_channel);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("手机商城");

        tv_count = findViewById(R.id.tv_count);
        gv_channel = findViewById(R.id.gv_channel);

        // 给购物车图片和返回按钮添加点击事件监听
        findViewById(R.id.iv_cart).setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);

        // 获取数据库帮助对象，并打开读写连接
        dbHelper = ShoppingDBHelper.getInstance(this);
        dbHelper.openReadLink();
        dbHelper.openWriteLink();

        // 加载购物车商品数量
        showCartInfoTotal();

        // 加载所有的商品数据到页面中
        showGoodsInfo();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "ShoppingChannelActivity 调用onDestroy方法");
        super.onDestroy();
        dbHelper.closeDatabaseLink();
    }

    /**
     * 这个方法在活动准备好和用户进行交互的时候调用
     * 其它页面跳转回该页面时，刷新购物车数量
     */
    @Override
    protected void onResume() {
        Log.d(TAG, "ShoppingChannelActivity 调用onResume方法");
        super.onResume();
        // 查询购物车商品总数，并展示
        showCartInfoTotal();
    }

    /**
     * 获取所有商品，并加载展示到页面中
     */
    private void showGoodsInfo() {
        // 从数据库中获取所有商品
        List<GoodsInfo> goodsList = dbHelper.getAllGoodsInfo();
        // 适配器
        GoodsAdapter adapter = new GoodsAdapter(this, goodsList, this);
        gv_channel.setAdapter(adapter);
    }

    /**
     * 商品添加到购物车
     * @param id 商品id
     * @param name 商品名称
     */
    @Override
    public void addToCart(int id, String name) {
        // 购物车数量加1
        int count = ++ShoppingApplication.getInstance().goodsCount;
        tv_count.setText(String.valueOf(count));

        // 添加记录到购物车表中
        dbHelper.insertCartInfo(id);
        // 弹出提示
        ToastUtil.show(this, "成功添加" + name + "到购物车中");
    }

    /**
     * 获取购物车商品数量，并展示在标题栏中
     */
    private void showCartInfoTotal() {
        int count = dbHelper.countCartInfo();
        // 将购物车商品数量加载到全局缓存中
        ShoppingApplication.getInstance().goodsCount = count;
        // 设置数量显示到标题控件中
        tv_count.setText(String.valueOf(count));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back: // 返回按钮
                // 结束当前页面
                finish();
                break;
            case R.id.iv_cart:
                // 跳转到购物车页面
                Intent intent = new Intent(this, ShoppingCartActivity.class);
                // 设置启动标志，避免多次返回同一页面的
                // 解释：https://blog.51cto.com/u_9420214/6833518
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }
}