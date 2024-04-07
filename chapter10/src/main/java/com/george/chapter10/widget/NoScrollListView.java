package com.george.chapter10.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class NoScrollListView extends ListView {
    public NoScrollListView(Context context) {
        super(context);
    }

    public NoScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScrollListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 重写onMeasure方法，自行设定视图的高度
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /*
        创建一个新的测量规格（MeasureSpec），用于指定视图的大小。具体来说：

        Integer.MAX_VALUE >> 2 是一个位运算操作，它将整数的最大值右移两位，
        即将其除以4。这样做是为了确保生成的高度规格不会超过整数的最大值，
        同时避免由于高度过大而可能导致的一些问题。
        因为 Integer.MAX_VALUE 表示整数的最大值，右移两位相当于将其除以4，
        因此得到的值仍然是一个非常大的数值，但不至于达到整数的最大值。

        MeasureSpec.AT_MOST 指定了测量规格的模式为最大值模式，即视图的大小不应超过指定的值。
         */
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
