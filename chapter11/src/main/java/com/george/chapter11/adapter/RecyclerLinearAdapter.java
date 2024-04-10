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

import java.util.List;

public class RecyclerLinearAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext; // 声明一个上下文对象
    private List<NewsInfo> mPublicList; // 公众号列表

    public RecyclerLinearAdapter(Context context, List<NewsInfo> publicList) {
        this.mContext = context;
        this.mPublicList = publicList;
    }

    /**
     * 创建列表项的视图持有者
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_linear, parent, false);
        return new ItemHolder(view);
    }

    /**
     * 绑定列表项的视图持有者
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemHolder itemHolder = (ItemHolder) holder;
        itemHolder.iv_pic.setImageResource(mPublicList.get(position).pic_id);
        itemHolder.tv_title.setText(mPublicList.get(position).title);
        itemHolder.tv_desc.setText(mPublicList.get(position).desc);
        itemHolder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "点击了列表项", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPublicList.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        // 声明列表项图标的图像视图
        private final ImageView iv_pic;
        // 声明列表项标题的文本视图
        private final TextView tv_title;
        // 声明列表项描述的文本视图
        private final TextView tv_desc;
        // 列表项布局
        private final LinearLayout ll_item;

        public ItemHolder(@NonNull View v) {
            super(v);
            iv_pic = v.findViewById(R.id.iv_pic);
            tv_title = v.findViewById(R.id.tv_title);
            tv_desc = v.findViewById(R.id.tv_desc);
            ll_item = v.findViewById(R.id.ll_item);
        }
    }
}
