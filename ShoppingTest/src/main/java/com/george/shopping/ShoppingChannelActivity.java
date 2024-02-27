package com.george.shopping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.george.shopping.database.ShoppingDBHelper;
import com.george.shopping.entity.GoodsInfo;
import com.george.shopping.utils.ToastUtil;

import java.util.List;

public class ShoppingChannelActivity extends AppCompatActivity implements View.OnClickListener {

    // 头部标题
    private TextView tv_title;
    // 表格布局容器
    private GridLayout gl_channel;
    // 购物车商品总数量
    private TextView tv_count;
    // 商品数据库帮助对象
    private ShoppingDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_channel);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("手机商城");

        tv_count = findViewById(R.id.tv_count);
        gl_channel = findViewById(R.id.gl_channel);

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

    /**
     * 获取所有商品，并加载展示到页面中
     */
    private void showGoodsInfo() {
        // 商品条目是一个线性布局，设置布局的宽度为屏幕的一半
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        // 线性布局的宽、高参数
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth / 2, LinearLayout.LayoutParams.WRAP_CONTENT);
        // 从数据库中获取所有商品
        List<GoodsInfo> goodsList = dbHelper.getAllGoodsInfo();

        // 清除布局控件下所有的内容
        gl_channel.removeAllViews();
        for (GoodsInfo goods : goodsList) {
            // 获取布局文件item_goods.xml的根视图
            View itemView = LayoutInflater.from(this).inflate(R.layout.item_goods, null);
            TextView tv_name = itemView.findViewById(R.id.tv_name);
            ImageView iv_thumb = itemView.findViewById(R.id.iv_thumb);
            TextView tv_price = itemView.findViewById(R.id.tv_price);
            Button btn_add = itemView.findViewById(R.id.btn_add);

            tv_name.setText(goods.name);
            iv_thumb.setImageURI(Uri.parse(goods.picPath));
            tv_price.setText(String.valueOf(goods.price));

            // 点击商品图片，跳转到商品详情页
            iv_thumb.setOnClickListener(v -> {
                Intent intent = new Intent(ShoppingChannelActivity.this, ShoppingDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("goods_id", goods.id);
                startActivity(intent);
            });
            // 点击“添加购物车”按钮，将商品添加到购物车
            btn_add.setOnClickListener(v -> {
                addToCart(goods.id, goods.name);
            });

            // 将商品列表视图，添加到网格布局
            gl_channel.addView(itemView, params);
        }
    }

    /**
     * 商品添加到购物车
     * @param id 商品id
     * @param name 商品名称
     */
    private void addToCart(int id, String name) {
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