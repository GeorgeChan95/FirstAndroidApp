package com.george.chapter10.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;


public class PieAnimation extends View {
    // 创建一个画笔对象
    private Paint mPaint = new Paint();
    // 当前绘制的角度
    private int mDrawingAngle = 0;
    // 声明一个处理器对象
    private Handler mHandler = new Handler(Looper.myLooper());
    // 是否正在播放动画
    public boolean isRunning = false;
    public PieAnimation(Context context) {
        super(context);

    }
    public PieAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint.setColor(Color.RED);
    }

    public void start() {
        isRunning = true;
        mHandler.post(mRefresh);
    }

    public void stop() {
        isRunning = false;
        mHandler.removeCallbacks(mRefresh);
    }

    Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            mDrawingAngle += 3;
            if (mDrawingAngle <= 270) { // 未绘制完成，最大绘制到270度
                invalidate();
                // 延迟若干时间后再次启动刷新任务
                mHandler.postDelayed(mRefresh, 100);
            } else {
                // 已绘制完成
                isRunning = false;
            }
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isRunning) {
            // 布局的实际宽度
            int width = getMeasuredWidth();
            // 布局的实际高度
            int height = getMeasuredHeight();
            // 视图的宽高取较小的那个作为扇形的直径
            int diameter = Math.min(width, height);
            // 创建扇形的矩形边界
            RectF rectf = new RectF((width - diameter) / 2, (height - diameter) / 2,
                    (width + diameter) / 2, (height + diameter) / 2);
            canvas.drawArc(rectf, 0, mDrawingAngle, true, mPaint);
        }
    }
}
