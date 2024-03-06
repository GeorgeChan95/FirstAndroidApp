package com.george.shopping.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.george.shopping.R;
import com.george.shopping.ShoppingDetailActivity;
import com.george.shopping.entity.GoodsInfo;
import com.george.shopping.interfaces.AddCartListener;

import java.util.List;

public class GoodsAdapter extends BaseAdapter {
    private Context context;
    private List<GoodsInfo> goodsList;
    private AddCartListener addCartListener;

    public GoodsAdapter(Context context, List<GoodsInfo> goodsList, AddCartListener addCartListener) {
        this.context = context;
        this.goodsList = goodsList;
        this.addCartListener = addCartListener;
    }

    @Override
    public int getCount() {
        return goodsList.size();
    }

    @Override
    public Object getItem(int position) {
        return goodsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_goods, null);
            holder = new ViewHolder();
            holder.tv_name = convertView.findViewById(R.id.tv_name);
            holder.iv_thumb = convertView.findViewById(R.id.iv_thumb);
            holder.tv_price = convertView.findViewById(R.id.tv_price);
            holder.btn_add = convertView.findViewById(R.id.btn_add);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        GoodsInfo goodsInfo = goodsList.get(position);
        holder.tv_name.setText(goodsInfo.name);
        holder.iv_thumb.setImageURI(Uri.parse(goodsInfo.picPath));
        holder.tv_price.setText(String.valueOf(goodsInfo.price));

        // 点击“添加购物车”按钮，将商品添加到购物车
        holder.btn_add.setOnClickListener(v -> addCartListener.addToCart(goodsInfo.id, goodsInfo.name));

        // 点击商品图片，跳转到商品详情页
        holder.iv_thumb.setOnClickListener(v -> {
            Intent intent = new Intent(context, ShoppingDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("goods_id", goodsInfo.id);
            context.startActivity(intent);
        });

        return convertView;
    }

    public class ViewHolder {
        public TextView tv_name;
        public ImageView iv_thumb;
        public TextView tv_price;
        public Button btn_add;
    }
}
