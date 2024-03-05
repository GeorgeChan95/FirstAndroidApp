package com.george.chapter08.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.george.chapter08.R;
import com.george.chapter08.entity.Planet;

import java.util.List;

public class PlanetBaseAdapter extends BaseAdapter {

    private Context mContext;
    private List<Planet> planetList;

    public PlanetBaseAdapter(Context mContext, List<Planet> planetList) {
        this.mContext = mContext;
        this.planetList = planetList;
    }

    /**
     * 获取列表项的个数
     * @return
     */
    @Override
    public int getCount() {
        return planetList.size();
    }

    /**
     * 获取列表项的数据
     * @param position 数据在列表中的索引位置
     * @return
     */
    @Override
    public Object getItem(int position) {
        return planetList.get(position);
    }

    /**
     * 获取列表项的编号
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 获取每项的展示视图，并对每项的内部控件进行业务处理
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            // 创建一个新的视图持有者
            holder = new ViewHolder();
            // 根据布局文件item_list.xml生成转换视图对象
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list, null);
            holder.iv_icon = convertView.findViewById(R.id.iv_icon);
            holder.tv_name = convertView.findViewById(R.id.tv_name);
            holder.tv_desc = convertView.findViewById(R.id.tv_desc);

            // 将视图持有者保存到转换视图当中
            convertView.setTag(holder);
        } else {
            // 从转换视图中获取之前保存的视图持有者
            holder = (ViewHolder) convertView.getTag();
        }

        Planet planet = planetList.get(position);
        holder.iv_icon.setImageResource(planet.image);
        holder.tv_name.setText(planet.name);
        holder.tv_desc.setText(planet.desc);
        return convertView;
    }

    class ViewHolder {
        public ImageView iv_icon;
        public TextView tv_name;
        public TextView tv_desc;
    }
}
