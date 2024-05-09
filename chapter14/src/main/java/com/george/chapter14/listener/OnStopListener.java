package com.george.chapter14.listener;

/**
 * 拍摄完成的监听器
 */
public interface OnStopListener {
    /**
     * 相机拍摄完成调用此方法
     * @param result
     */
    void onStop(String result);
}
