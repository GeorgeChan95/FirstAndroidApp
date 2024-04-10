package com.george.chapter11.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 具体见：https://www.jianshu.com/p/bcbfb84fe6d1
 */
public class SpacesDecoration extends RecyclerView.ItemDecoration {
    // 空白间隔
    private int space;
    private Drawable mDivider;
    private Context mContext;

    public SpacesDecoration(Context context, int space, int redId) {
        this.space = space;
        this.mContext = context;
        mDivider = context.getResources().getDrawable(redId);
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

        // 绘制分割线
        // 获取了 RecyclerView 左边缘和右边缘的坐标，减去了 padding 的宽度。
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        // 获取 RecyclerView 中子项的数量
        int childCount = parent.getChildCount();
        // 遍历 RecyclerView 中的每一个子项
        for (int i = 0; i < childCount; i++) {
            // 获取了当前子项的 View 对象
            View child = parent.getChildAt(i);
            // 获取了当前子项的布局参数
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            // 计算了分割线顶部的坐标，即当前子项底部坐标加上底部 margin
            int top = child.getBottom() + params.bottomMargin;
            // 计算了分割线底部的坐标，即顶部坐标加上分割线的高度
            int bottom = top + mDivider.getIntrinsicHeight();
            // 设置了分割线的边界，即左上角和右下角的坐标
            mDivider.setBounds(left, top, right, bottom);
            // 使用 Canvas 对象 c 来绘制分割线
            mDivider.draw(c);
        }
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
