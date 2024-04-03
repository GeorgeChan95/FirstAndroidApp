package com.george.chapter10.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class DrawRelativeLayout extends RelativeLayout {
    // 绘制类型
    private int mDrawType = 0;
    // 创建一个画笔对象
    private Paint paint = new Paint();
    // 线宽
    private float mStrokeWidth = 3;

    public DrawRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 设置画笔为无锯齿
        paint.setAntiAlias(true);
        // 设置画笔为防抖动
        paint.setDither(true);
        // 设置画笔颜色
        paint.setColor(Color.BLACK);
        // 设置画笔线宽
        paint.setStrokeWidth(mStrokeWidth);
        // 设置画笔类型: STROKE表示空心，FILL表示实心
        // 如果设置成FILL，整个绘画区域都是黑色的。
        paint.setStyle(Paint.Style.STROKE);
    }

    /**
     * onDraw在方法绘制下级视图之前调用
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // DrawRelativeLayout布局的实际宽度
        int width = getMeasuredWidth();
        // DrawRelativeLayout布局的实际高度
        int height = getMeasuredHeight();
        if (width > 0 && height > 0) {
            switch (mDrawType) {
                case 1:// 绘制矩形
                    Rect rect = new Rect(0, 0, width, height);
                    // 在画布上绘制矩形
                    canvas.drawRect(rect, paint);
                    break;
                case 2:// 绘制圆角矩形
                    RectF rectF = new RectF(0, 0, width, height);
                    // 在画布上绘制圆角矩形
                    canvas.drawRoundRect(rectF, 30, 30, paint);
                    break;
                case 3:// 绘制圆圈
                    float radius = Math.min(width, height)/2 - mStrokeWidth;
                    // 在画布上绘制圆圈
                    canvas.drawCircle(width/2, height/2, radius, paint);
                    break;
                case 4:// 绘制椭圆
                    RectF rf = new RectF(0, 0, width, height);
                    // 在画布上绘制椭圆
                    canvas.drawOval(rf, paint);
                    break;
                case 5:// 绘制矩形及其对角线
                    rect = new Rect(0, 0, width, height);
                    canvas.drawRect(rect, paint);
                    canvas.drawLine(0, 0, width, height, paint);
                    canvas.drawLine(0, height, width, 0, paint);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * dispatchDraw在绘制下级视图之后调用
     * @param canvas
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        // DrawRelativeLayout布局的实际宽度
        int width = getMeasuredWidth();
        // DrawRelativeLayout布局的实际高度
        int height = getMeasuredHeight();

        switch (mDrawType) {
            case 6:
                Rect rect = new Rect(0, 0, width, height);
                // 在画布上绘制矩形
                canvas.drawRect(rect, paint);
                canvas.drawLine(0, 0, width, height, paint);
                canvas.drawLine(0, height, width, 0, paint);
                break;
            default:
                break;
        }
    }


    public void setDrawType(int type) {
        // 设置背景色，目的是把画布擦干净
        setBackgroundColor(Color.WHITE);
        mDrawType = type;
        // 立即重新绘图，此时会触发 onDraw 方法和 dispatchDraw 方法
        invalidate();
    }
}
