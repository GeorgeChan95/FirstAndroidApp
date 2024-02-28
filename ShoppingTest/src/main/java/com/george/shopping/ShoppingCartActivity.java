package com.george.shopping;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.george.shopping.database.ShoppingDBHelper;
import com.george.shopping.entity.CartInfo;
import com.george.shopping.entity.GoodsInfo;
import com.george.shopping.utils.ToastUtil;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCartActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "GeorgeTag";

    // 购物车数量
    private TextView tv_count;
    // 购物车商品内容
    private LinearLayout ll_content;
    // 购物车为空显示组件
    private LinearLayout ll_empty;
    // 动态填充商品列表组件
    private LinearLayout ll_cart;
    // 购物车商品总价组件
    private TextView tv_total_price;
    // 购物车数据库帮助对象
    private ShoppingDBHelper dbHelper;
    // 声明一个根据商品编号查找商品信息的映射，把商品信息缓存起来，这样不用每一次都去查询数据库
    private Map<Integer, GoodsInfo> mGoodsMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "ShoppingCartActivity 调用onCreate方法");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        // 设置标题
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("购物车");
        // 获取组件
        tv_count = findViewById(R.id.tv_count);
        ll_content = findViewById(R.id.ll_content);
        ll_empty = findViewById(R.id.ll_empty);
        ll_cart = findViewById(R.id.ll_cart);
        tv_total_price = findViewById(R.id.tv_total_price);

        // 获取数据库帮助对象,打开数据库读写连接
        dbHelper = ShoppingDBHelper.getInstance(this);
        dbHelper.openWriteLink();
        dbHelper.openReadLink();

        // 返回按钮
        findViewById(R.id.iv_back).setOnClickListener(this);
        // 清空按钮
        findViewById(R.id.btn_clear).setOnClickListener(this);
        // 结算按钮
        findViewById(R.id.btn_settle).setOnClickListener(this);
        // 跳转手机商场按钮
        findViewById(R.id.btn_shopping_channel).setOnClickListener(this);
    }

    /**
     * 这个方法在活动准备好和用户进行交互的时候调用
     */
    @Override
    protected void onResume() {
        Log.d(TAG, "ShoppingCartActivity 调用onResume方法");
        super.onResume();
        // 显示商品数量
        showCount();
        // 显示购物车数据列表
        showGoodsInfo();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "ShoppingCartActivity 调用onDestroy方法");
        super.onDestroy();
    }

    /**
     * 加载购物车商品信息，并填充到页面中
     */
    private void showGoodsInfo() {
        // 先清空商品展示区域所有的内容
        ll_cart.removeAllViews();
        // 查询购物车数据库中所有的商品记录
        List<CartInfo> cartList = dbHelper.getAllCartInfo();
        if (cartList.isEmpty()) {
            Log.d(TAG, "购物车数据为空");
            return;
        }

        for (CartInfo cartInfo : cartList) {
            GoodsInfo goodsInfo = dbHelper.queryGoodsInfoById(cartInfo.goodsId);
            mGoodsMap.put(goodsInfo.id, goodsInfo);

            View itemView = LayoutInflater.from(this).inflate(R.layout.item_cart, null);
            ImageView iv_thumb = itemView.findViewById(R.id.iv_thumb);
            TextView tv_name = itemView.findViewById(R.id.tv_name);
            TextView tv_desc = itemView.findViewById(R.id.tv_desc);
            TextView tv_count = itemView.findViewById(R.id.tv_count);
            TextView tv_price = itemView.findViewById(R.id.tv_price);
            TextView tv_total_price = itemView.findViewById(R.id.tv_total_price);

            iv_thumb.setImageBitmap(BitmapFactory.decodeFile(goodsInfo.picPath));
            tv_name.setText(goodsInfo.name);
            tv_desc.setText(goodsInfo.description);
            tv_count.setText(String.valueOf(cartInfo.count));
            tv_price.setText(String.valueOf(goodsInfo.price));
            tv_total_price.setText(String.valueOf(cartInfo.count * goodsInfo.price));

            // 给商品行添加长按事件。长按商品行就删除该商品
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingCartActivity.this);
                    builder.setMessage("是否确认从购物车删除 " + goodsInfo.name + " ?");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 页面中移除该列
                            ll_cart.removeView(itemView);
                            // 从购物车表删除该商品
                            removeCartInfo(cartInfo);
                        };
                    });
                    builder.setNegativeButton("否", null);
                    builder.create().show();
                    return true;
                }
            });

            // 给商品行添加点击事件。点击商品行跳到商品的详情页
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShoppingCartActivity.this, ShoppingDetailActivity.class);
                    intent.putExtra("goods_id", goodsInfo.id);
                    startActivity(intent);
                }
            });

            // 往购物车列表添加商品
            ll_cart.addView(itemView);
        }
        // 重新计算购物车中的商品总金额
        refreshTotalPrice(cartList);
    }

    /**
     * 购物车删除商品
     * @param cartInfo 购物车商品信息
     */
    private void removeCartInfo(CartInfo cartInfo) {
        // 从数据库中删除商品
        boolean res = dbHelper.deleteFromCartInfo(cartInfo.id);
        if (res) {
            // 修改全局参数中商品数量
            ShoppingApplication.getInstance().goodsCount -= cartInfo.count;
            // 显示商品数量
            showCount();
            // 显示购物车数据列表
            showGoodsInfo();
            ToastUtil.show(this, "删除成功");
            return;
        }
        ToastUtil.show(this, "操作异常");
    }

    /**
     * 刷新购物车商品总价格
     * @param cartList 购物车数据集
     */
    private void refreshTotalPrice(List<CartInfo> cartList) {
        float total = 0f;
        for (CartInfo info : cartList) {
            int count = info.count;
            int goodsId = info.goodsId;
            GoodsInfo goodsInfo = mGoodsMap.get(goodsId);
            total += goodsInfo.price * count;
        }
        tv_total_price.setText(String.valueOf(total));
    }

    /**
     * 显示购物车商品数量
     */
    private void showCount() {
        int count = ShoppingApplication.getInstance().goodsCount;
        if (count <= 0) {
            ll_content.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        } else {
            ll_content.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
            // 清空动态填充商品数据组件
            ll_cart.removeAllViews();
        }
        tv_count.setText(String.valueOf(count));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                // 点击返回按钮，结束当前页面
                finish();
                break;
            case R.id.btn_clear:
                // 清空购物车
                boolean res = dbHelper.deleteAllCartInfo();
                if (res) {
                    ShoppingApplication.getInstance().goodsCount = 0;
                    showCount();
                    ToastUtil.show(this, "购物车已清空");
                }
                break;
            case R.id.btn_settle:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("结算");
                builder.setMessage("暂未开发此功能");
                builder.setPositiveButton("我知道了", null);
                builder.create().show();
                break;
            case R.id.btn_shopping_channel:
                // 从购物车页面跳转到商场页面
                Intent intent = new Intent(this, ShoppingChannelActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }
}