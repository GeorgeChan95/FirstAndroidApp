package com.george.chapter17.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.george.chapter17.R;
import com.george.chapter17.entity.BlueDevice;

import java.util.List;

public class BlueListAdapter extends BaseAdapter {
    private static final String TAG = "GeorgeTag";
    private Context mContext;
    private List<BlueDevice> mBlueList;
    private String[] mStateArray = {"未绑定", "绑定中", "已绑定", "已连接"};
    private String[] mBleStateArray = {"未连接", "已连接"};

    public BlueListAdapter(Context context, List<BlueDevice> blueList) {
        this.mContext = context;
        this.mBlueList = blueList;
    }

    @Override
    public int getCount() {
        return mBlueList.size();
    }

    @Override
    public Object getItem(int position) {
        return mBlueList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_bluetooth, null);
            holder.tv_blue_name = convertView.findViewById(R.id.tv_blue_name);
            holder.tv_blue_address = convertView.findViewById(R.id.tv_blue_address);
            holder.tv_blue_state = convertView.findViewById(R.id.tv_blue_state);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BlueDevice blueDevice = mBlueList.get(position);
        holder.tv_blue_name.setText(blueDevice.name);
        holder.tv_blue_address.setText(blueDevice.address);
        // 显示蓝牙设备状态
        if (blueDevice.state >= 10) {
            holder.tv_blue_state.setText(mStateArray[blueDevice.state-10]);
        } else {
            holder.tv_blue_state.setText(mBleStateArray[blueDevice.state]);
        }
        return convertView;
    }

    public final class ViewHolder {
        public TextView tv_blue_name;
        public TextView tv_blue_address;
        public TextView tv_blue_state;
    }
}
