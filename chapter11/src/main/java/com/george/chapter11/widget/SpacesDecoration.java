package com.george.chapter11.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 具体见：https://www.jianshu.com/p/bcbfb84fe6d1
 */
public class SpacesDecoration extends RecyclerView.ItemDecoration {
    // 空白间隔
    private int space;

    public SpacesDecoration(int space) {
        this.space = space;
    }

    /**
     * 设置每个item偏移量
     * @param outRect 方形的大小，决定item与group的边距和item之间的边距
     * @param view
     * @param parent
     * @param state
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0,0,0, space);
    }

    /**
     * 绘制背景色
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
//        Paint paint = new Paint();
//        paint.setColor(Color.BLUE);
//        c.drawRect(0, 0, 3000, 100, paint);
    }

    /**
     * 绘制前景色
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
//        Paint paint = new Paint();
//        paint.setColor(Color.RED);
//        c.drawRect(0, 100, 3000, 200, paint);
    }
}
