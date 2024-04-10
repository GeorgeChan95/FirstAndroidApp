package com.george.chapter11.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.george.chapter11.R;
import com.george.chapter11.entity.NewsInfo;
import com.george.chapter11.listener.OnItemClickListener;
import com.george.chapter11.listener.OnItemLongClickListener;

import java.util.List;
import java.util.zip.Inflater;

public class RecyclerGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnItemClickListener, OnItemLongClickListener{
    // 上下文对象
    private Context mContext;
    private List<NewsInfo> mGoodsList;

    public RecyclerGridAdapter(Context context, List<NewsInfo> goodsList) {
        this.mContext = context;
        this.mGoodsList = goodsList;
    }

    private OnItemClickListener mClickListener;
    private OnItemLongClickListener mLongClickListener;

    public void setClickListener(OnItemClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    public void setLongClickListener(OnItemLongClickListener longClickListener) {
        this.mLongClickListener = longClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_grid, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemHolder itemHolder = (ItemHolder) holder;
        itemHolder.iv_pic.setImageResource(mGoodsList.get(position).pic_id);
        itemHolder.tv_title.setText(mGoodsList.get(position).title);

        // 设置列表项的点击事件，接口需要自己实现
        itemHolder.ll_item.setOnClickListener(v -> {
            if (mClickListener != null) {
                mClickListener.onItemClick(v, position);
            }
        });
        // 设置列表项的长按事件
        itemHolder.ll_item.setOnLongClickListener(v -> {
            if (mLongClickListener != null) {
                mLongClickListener.onItemLongClick(v, position);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mGoodsList.size();
    }

    /**
     * 处理列表项的点击事件
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(View view, int position) {
        String desc = String.format("您点击了第%d项，栏目名称是%s", position + 1,
                mGoodsList.get(position).title);
        Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
    }

    /**
     * 处理列表项的长按事件
     * @param view
     * @param position
     */
    @Override
    public void onItemLongClick(View view, int position) {
        String desc = String.format("您长按了第%d项，栏目名称是%s", position + 1,
                mGoodsList.get(position).title);
        Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
    }

    // 定义列表项的视图持有者
    public class ItemHolder extends RecyclerView.ViewHolder {
        public LinearLayout ll_item; // 声明列表项的线性布局
        public ImageView iv_pic; // 声明列表项图标的图像视图
        public TextView tv_title; // 声明列表项标题的文本视图

        public ItemHolder(View v) {
            super(v);
            ll_item = v.findViewById(R.id.ll_item);
            iv_pic = v.findViewById(R.id.iv_pic);
            tv_title = v.findViewById(R.id.tv_title);
        }
    }
}
