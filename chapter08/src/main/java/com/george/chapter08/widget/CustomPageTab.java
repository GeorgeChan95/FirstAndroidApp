package com.george.chapter08.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;

import androidx.viewpager.widget.PagerTabStrip;

import com.george.chapter08.R;
import com.george.chapter08.util.Utils;

public class CustomPageTab extends PagerTabStrip {
    private static final String TAG = "GeorgeTag";

    private int textColor = Color.BLACK;
    private int textSize = 15;

    public CustomPageTab(Context context) {
        super(context);
    }

    /**
     * 获取xml文件中定义的属性，以及它们的值
     * @param context
     * @param attrs  从XML 中传递过来的属性
     */
    public CustomPageTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            // 根据CustomPagerTab定义的属性，从XML文件中获取，并将获取到的属性以及值存到数组中
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomPageTab);
            // 根据属性描述定义，获取XML文件中的文本颜色
            textColor = typedArray.getColor(R.styleable.CustomPageTab_android_textColor, textColor);
            // 根据属性描述定义，获取XML文件中的文本大小
            // getDimension得到的是px值，需要转换为sp值
            textSize = Utils.px2sp(context, typedArray.getDimensionPixelSize(R.styleable.CustomPageTab_android_textSize, textSize));
            int size = Utils.px2sp(context, typedArray.getDimension(R.styleable.CustomPageTab_android_textSize, textSize));

            String log = String.format("textColor=%d, textSize=%d, size=%d", textColor, textSize, size);
            Log.d(TAG, log);

            // 回收属性数组描述，释放它所占用的资源。
            typedArray.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setTextColor(textColor);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }
}
