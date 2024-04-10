package com.george.chapter11.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.george.chapter11.R;
import com.george.chapter11.entity.NewsInfo;
import com.george.chapter11.listener.OnItemClickListener;
import com.george.chapter11.listener.OnItemDeleteClickListener;
import com.george.chapter11.listener.OnItemLongClickListener;

import java.util.List;

public class LinearDynamicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, View.OnLongClickListener{
    private Context mContext; // 声明一个上下文对象
    private List<NewsInfo> mPublicList;

    private int CLICK = 0; // 正常点击
    private int DELETE = 1; // 点击了删除按钮

    public LinearDynamicAdapter(Context context, List<NewsInfo> publicList) {
        mContext = context;
        mPublicList = publicList;
    }

    // 声明列表项的点击监听器对象
    private OnItemClickListener mOnItemClickListener;
    // 声明列表项的长按监听器对象
    private OnItemLongClickListener mOnItemLongClickListener;
    // 声明列表项的删除监听器对象
    private OnItemDeleteClickListener mOnItemDeleteClickListener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 根据布局文件item_linear.xml生成视图对象
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_linear, parent, false);
        return new ItemHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        NewsInfo item = mPublicList.get(position);
        ItemHolder holder = (ItemHolder) vh;
        holder.iv_pic.setImageResource(item.pic_id);
        holder.tv_title.setText(item.title);
        holder.tv_desc.setText(item.desc);
        // 控制删除按钮的隐藏和显示
        holder.tv_delete.setVisibility(item.isPressed ? View.VISIBLE : View.GONE);
        holder.tv_delete.setId(item.id * 10 + DELETE);
        holder.tv_delete.setOnClickListener(this);
        holder.ll_item.setId(item.id * 10 + CLICK);
        // 列表项的点击事件需要自己实现
        holder.ll_item.setOnClickListener(this);
        // 列表项的长按事件需要自己实现
        holder.ll_item.setOnLongClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mPublicList.size();
    }

    @Override
    public void onClick(View v) {
        int position = getPosition((int) v.getId() / 10);
        int type = (int) v.getId() % 10;

        if (type == CLICK) { // 正常点击，则触发点击监听器的onItemClick方法
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, position);
            }
        } else if (type == DELETE) { // 点击了删除按钮，则触发删除监听器的onItemDeleteClick方法
            if (mOnItemDeleteClickListener != null) {
                mOnItemDeleteClickListener.onItemDeleteClick(v, position);
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        int position = getPosition((int) v.getId() / 10);
        if (mOnItemLongClickListener != null) {
            mOnItemLongClickListener.onItemLongClick(v, position);
        }
        return true;
    }

    // 根据列表项编号获取当前的位置序号
    private int getPosition(int item_id) {
        int pos = 0;
        for (int i = 0; i < mPublicList.size(); i++) {
            if (mPublicList.get(i).id == item_id) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    /**
     * 给监听器赋值
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    public void setOnItemDeleteClickListener(OnItemDeleteClickListener listener) {
        this.mOnItemDeleteClickListener = listener;
    }

    // 定义列表项的视图持有者
    public class ItemHolder extends RecyclerView.ViewHolder {
        public LinearLayout ll_item; // 声明列表项的线性布局
        public ImageView iv_pic; // 声明列表项图标的图像视图
        public TextView tv_title; // 声明列表项标题的文本视图
        public TextView tv_desc; // 声明列表项描述的文本视图
        public TextView tv_delete; // 声明列表项删除按钮的文本视图

        public ItemHolder(View v) {
            super(v);
            ll_item = v.findViewById(R.id.ll_item);
            iv_pic = v.findViewById(R.id.iv_pic);
            tv_title = v.findViewById(R.id.tv_title);
            tv_desc = v.findViewById(R.id.tv_desc);
            tv_delete = v.findViewById(R.id.tv_delete);
        }

    }
}
