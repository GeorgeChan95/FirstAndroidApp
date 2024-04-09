package com.george.chapter11.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.george.chapter11.R;

public class TabThirdFragment extends Fragment {

    // 声明一个视图对象
    private View mView;
    // 声明一个上下文对象
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 获取活动页面的上下文
        mContext = getActivity();
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_tab_third, container, false);
        TextView tv_first = mView.findViewById(R.id.tv_third);
        tv_first.setText("这是购物车页");
        return mView;
    }
}