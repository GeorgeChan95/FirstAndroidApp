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
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
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
