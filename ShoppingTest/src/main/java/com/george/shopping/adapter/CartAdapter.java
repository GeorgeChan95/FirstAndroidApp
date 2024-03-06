package com.george.shopping.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.george.shopping.R;
import com.george.shopping.entity.CartInfo;

import java.util.List;

public class CartAdapter extends BaseAdapter {
    private Context context;
    private List<CartInfo> cartInfos;

    public CartAdapter(Context context, List<CartInfo> cartInfos) {
        this.context = context;
        this.cartInfos = cartInfos;
    }

    @Override
    public int getCount() {
        return cartInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return cartInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cart, null);
            holder = new ViewHolder();
            holder.iv_thumb = convertView.findViewById(R.id.iv_thumb);
            holder.tv_name = convertView.findViewById(R.id.tv_name);
            holder.tv_desc = convertView.findViewById(R.id.tv_desc);
            holder.tv_count = convertView.findViewById(R.id.tv_count);
            holder.tv_price = convertView.findViewById(R.id.tv_price);
            holder.tv_total_price = convertView.findViewById(R.id.tv_total_price);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CartInfo cartInfo = cartInfos.get(position);
        holder.iv_thumb.setImageBitmap(BitmapFactory.decodeFile(cartInfo.goods.picPath));
        holder.tv_name.setText(cartInfo.goods.name);
        holder.tv_desc.setText(cartInfo.goods.description);
        holder.tv_count.setText(String.valueOf(cartInfo.count));
        holder.tv_price.setText(String.valueOf(cartInfo.goods.price));
        // 设置商品总价
        holder.tv_total_price.setText(String.valueOf(cartInfo.count * cartInfo.goods.price));
        return convertView;
    }

    public final class ViewHolder {
        public ImageView iv_thumb;
        public TextView tv_name;
        public TextView tv_desc;
        public TextView tv_count;
        public TextView tv_price;
        public TextView tv_total_price ;
    }
}
