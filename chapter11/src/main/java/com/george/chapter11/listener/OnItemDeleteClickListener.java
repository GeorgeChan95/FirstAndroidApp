package com.george.chapter11.listener;

import android.view.View;

/**
 * 定义一个循环视图列表项的删除监听器接口
 */
public interface OnItemDeleteClickListener {
    void onItemDeleteClick(View view, int position);
}
