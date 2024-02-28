package com.george.shopping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.george.shopping.database.ShoppingDBHelper;
import com.george.shopping.entity.GoodsInfo;
import com.george.shopping.utils.ToastUtil;

public class ShoppingDetailActivity extends AppCompatActivity implements View.OnClickListener {

    // 商品名称
    private TextView tv_title;
    // 商品图片
    private ImageView iv_goods_pic;
    // 价格
    private TextView tv_goods_price;
    // 描述
    private TextView tv_goods_desc;
    // 数据库帮助对象
    private ShoppingDBHelper dbHelper;
    // 购物车商品总数量
    private TextView tv_count;
    // 商品ID，从其它页面跳转过来接收到的参数
    private int goods_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_detail);
        // 从全局参数中获取购物车商品总数量
        int count = ShoppingApplication.getInstance().goodsCount;
        dbHelper = ShoppingDBHelper.getInstance(this);

        // 标题，显示商品名称
        tv_title = findViewById(R.id.tv_title);
        // 购物车商品数量
        tv_count = findViewById(R.id.tv_count);
        tv_count.setText(String.valueOf(count));
        // 商品图片
        iv_goods_pic = findViewById(R.id.iv_goods_pic);
        // 价格
        tv_goods_price = findViewById(R.id.tv_goods_price);
        // 描述
        tv_goods_desc = findViewById(R.id.tv_goods_desc);


        // 返回按钮
        findViewById(R.id.iv_back).setOnClickListener(this);
        // 购物车图标
        findViewById(R.id.iv_cart).setOnClickListener(this);
        // 添加到购物车按钮
        findViewById(R.id.btn_add_cart).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showGoodsDetail();
    }

    /**
     * 显示商品明细
     */
    private void showGoodsDetail() {
        goods_id = getIntent().getIntExtra("goods_id", 0);
        if (goods_id == 0) {
            ToastUtil.show(this, "查看商品详情异常");
            return;
        }
        GoodsInfo goodsInfo = dbHelper.queryGoodsInfoById(goods_id);
        tv_title.setText(goodsInfo.name);
        iv_goods_pic.setImageURI(Uri.parse(goodsInfo.picPath));
        tv_goods_price.setText(String.valueOf(goodsInfo.price));
        tv_goods_desc.setText(goodsInfo.description);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back: // 返回按钮
                // 关闭当前页面
                finish();
                break;
            case R.id.iv_cart: // 购物车图片按钮
                Intent intent = new Intent(this, ShoppingCartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.btn_add_cart: // 添加到购物车按钮
                addToCart(goods_id);
                break;
        }
    }

    /**
     * 商品添加到购物车
     * @param goodsId 商品id
     */
    private void addToCart(int goodsId) {
        // 购物车数量加1
        int count = ++ShoppingApplication.getInstance().goodsCount;
        tv_count.setText(String.valueOf(count));

        String goodsName = tv_title.getText().toString();
        // 添加记录到购物车表中
        dbHelper.insertCartInfo(goodsId);
        // 弹出提示
        ToastUtil.show(this, "成功添加" + goodsName + "到购物车中");
    }
}