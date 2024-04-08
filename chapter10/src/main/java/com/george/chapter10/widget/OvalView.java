package com.george.chapter10.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class OvalView extends View {
    // 创建一个画笔对象
    private Paint mPaint = new Paint();
    // 当前绘制的角度
    private int mDrawingAngle = 0;

    public OvalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 设置画笔的颜色
        mPaint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制角度增加30度
        mDrawingAngle += 30;
        // 获取布局的实际宽度
        int width = getMeasuredWidth();
        // 获取布局的实际高度
        int height = getMeasuredHeight();
        // 设置绘制扇形的矩形边界
        RectF rectF = new RectF(0, 0, width, height);
        // 在画布上绘制指定角度的扇形。第四个参数为true表示绘制扇形，为false表示绘制圆弧
        canvas.drawArc(rectF, 0, mDrawingAngle, true, mPaint);
    }
}
