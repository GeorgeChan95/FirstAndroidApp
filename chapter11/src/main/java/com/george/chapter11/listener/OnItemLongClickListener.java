package com.george.chapter11.listener;

import android.view.View;

/**
 * 定义长按动作接口，响应长按事件
 * 使用接口，抽取公共方法，方便多个功能复用
 */
public interface OnItemLongClickListener {

    /**
     * 长按事件的响应方法
     * @param view
     * @param position
     */
    void onItemLongClick(View view, int position);
}
