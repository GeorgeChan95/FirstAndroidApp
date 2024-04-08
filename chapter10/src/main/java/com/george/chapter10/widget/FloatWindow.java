package com.george.chapter10.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.george.chapter10.interfaces.FloatClickListener;

public class FloatWindow extends View {
    private static final String TAG = "GeorgeTag";
    private Context mContext;
    // 是否正在显示
    private boolean isShowing = false;
    // 声明一个悬浮窗的点击监听器对象
    private FloatClickListener mListener;
    // 从系统服务中获取窗口管理器，后续通过窗口管理器添加悬浮窗
    private WindowManager manager;
    // 悬浮窗布局参数
    private static WindowManager.LayoutParams wmLayoutParams;
    // 悬浮窗视图对象
    public View mContentView;
    // 触摸点在屏幕上的横纵坐标
    private float mScreenX, mScreenY;
    // 上次触摸点的横纵坐标
    private float mLastX, mLastY;
    // 按下点的横纵坐标
    private float mDownX, mDownY;

    /**
     * 传入应用实例Application上下文对象
     * 初始化窗口管理
     * 初始化悬浮窗布局管理器
     * @param context
     */
    public FloatWindow(Context context) {
        super(context);
        // 从系统服务中获取窗口管理器，后续将通过该管理器添加悬浮窗
        manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wmLayoutParams == null) {
            wmLayoutParams = new WindowManager.LayoutParams();
        }
        mContext = context;
    }


    /**
     * 设置悬浮窗的内容布局
     * @param layoutId
     */
    public void setLayout(int layoutId) {
        // 从指定资源编号的布局文件中获取内容视图对象
        mContentView = LayoutInflater.from(mContext).inflate(layoutId, null);
        mContentView.setOnTouchListener((v, event) -> {
            // 获取事件在屏幕上的原始位置
            mScreenX = event.getRawX(); // 观察数据是否变化，观察结果：此方法会多次调用，mScreenX会变化
            mScreenY = event.getRawY();
            if (event.getAction() == MotionEvent.ACTION_DOWN) { // 在悬浮框按下操作
                mDownX = mScreenX;
                mDownY = mScreenY;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) { // 在悬浮框按下后拖动操作
                updateViewPosition();
            } else if (event.getAction() == MotionEvent.ACTION_UP) { // 松开手指
                updateViewPosition(); // 更新视图位置
                if (Math.abs(mScreenX - mDownX) < 3 && Math.abs(mScreenY - mDownY) < 3) {
                    if (mListener != null) {
                        mListener.onFloatClick(v);
                    }
                }
            }
            mLastX = mScreenX;
            mLastY = mScreenY;
            return true;
        });
    }

    /**
     * 更新悬浮窗的视图位置
     */
    private void updateViewPosition() {
        // 此处不能直接转为整型，因为小数部分会被截掉，重复多次后就会造成偏移越来越大
        wmLayoutParams.x = Math.round(wmLayoutParams.x + mScreenX - mLastX);
        wmLayoutParams.y = Math.round(wmLayoutParams.y + mScreenY - mLastY);
        // 更新视图内容的布局参数
        manager.updateViewLayout(mContentView, wmLayoutParams);
    }

    /**
     * 显示悬浮窗
     * @param gravity
     */
    public void show(int gravity) {
        if (mContentView != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                // 注意TYPE_SYSTEM_ALERT从Android8.0开始被舍弃了
                wmLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            } else {
                // 从Android8.0开始悬浮窗要使用TYPE_APPLICATION_OVERLAY
                wmLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY; // 这里不要写错了
            }
            // 窗口的像素点格式
            wmLayoutParams.format = PixelFormat.RGBA_8888;
            // 窗口的行为准则，FLAG_NOT_FOCUSABLE：不能抢占焦点，即：不接受任何按键和按钮事件
            wmLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            // 1.0为完全不透明，0.0为完全透明
            wmLayoutParams.alpha = 1.0f;
            // 内部视图的对其方式
            wmLayoutParams.gravity = gravity;
            wmLayoutParams.x = 0;
            wmLayoutParams.y = 0;
            // 设置悬浮窗宽度和高度自适应
            wmLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            wmLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            // 使用窗口管理器添加自定义布局，然后屏幕上就能看到悬浮窗了
            manager.addView(mContentView, wmLayoutParams);
            isShowing = true;
        }
    }

    /**
     * 关闭悬浮窗
     */
    public void close() {
        if (mContentView != null) {
            // 使用窗口管理器移除自定义布局
            manager.removeView(mContentView);
            isShowing = false;
        }
    }

    /**
     * 判断悬浮窗是否打开
     * @return
     */
    public boolean isShow() {
        return isShowing;
    }

    /**
     * 设置悬浮窗的点击监听器
     * @param listener
     */
    public void setOnFloadClickListener(FloatClickListener listener) {
        this.mListener = listener;
    }
}
