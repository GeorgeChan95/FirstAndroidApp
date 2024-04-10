package com.george.chapter11.listener;

import android.view.View;

/**
 * 定义点击动作接口，响应点击事件
 * 使用接口，抽取公共方法，方便多个功能复用
 */
public interface OnItemClickListener {
    /**
     * 点击事件的响应方法
     * @param view
     * @param position
     */
    void onItemClick(View view, int position);
}
