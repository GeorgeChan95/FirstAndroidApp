package com.george.chapter17.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.george.chapter17.R;

import java.util.List;
import java.util.Map;

public class ScanListAdapter extends BaseAdapter {
    private List<ScanResult> mScanList; // wifi扫描结果列表
    private Map<String, ScanResult> mRttMap; // RTT节点映射
    private Context mContext; // 声明一个上下文对象

    public ScanListAdapter(Context context, List<ScanResult> scanList, Map<String, ScanResult> rttMap) {
        mContext = context;
        mScanList = scanList;
        mRttMap = rttMap;
    }

    @Override
    public int getCount() {
        return mScanList.size();
    }

    @Override
    public Object getItem(int position) {
        return mScanList.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_scan, null);
            holder.tv_name = convertView.findViewById(R.id.tv_name);
            holder.tv_mac = convertView.findViewById(R.id.tv_mac);
            holder.tv_rtt = convertView.findViewById(R.id.tv_rtt);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ScanResult scanResult = mScanList.get(position);
        holder.tv_name.setText(scanResult.SSID);
        holder.tv_mac.setText(scanResult.BSSID);
        holder.tv_rtt.setText(mRttMap.containsKey(scanResult.BSSID) ? "是" : "否");
        return convertView;
    }

    public final class ViewHolder {
        public TextView tv_name;
        public TextView tv_mac;
        public TextView tv_rtt;
    }
}
