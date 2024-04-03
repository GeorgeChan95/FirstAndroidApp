package com.george.chapter10.util;

import android.app.Activity;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MeasureUtil {
    /**
     * 获取文本内容的长度
     * @param text 文本字符串
     * @param textSize 字体大小 sp
     * @return
     */
    public static float getTextWidth(String text, float textSize) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        }
        // 创建文本画笔对象
        Paint paint = new Paint();
        // 设置字体大小
        paint.setTextSize(textSize);
        // 计算文本宽度
        float width = paint.measureText(text);
        return width;
    }

    /**
     * 获取文本内容的高度
     * @param text 文本字符串
     * @param textSize 字体大小 sp
     * @return
     */
    public static float getTextHeigth(String text, float textSize) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        }
        // 创建文本画笔对象
        Paint paint = new Paint();
        // 设置画笔的文本大小
        paint.setTextSize(textSize);
        // 获取画笔默认字体的度量衡
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        // 获取文本自身的高度 (字符底部与基线的距离 - 字符顶部与基线距离)
        float textHeight = fontMetrics.descent - fontMetrics.ascent;
        // 获取文本所在行的行高 (字符底部与基线的距离 - 字符顶部与基线距离 + 行间距)
        float textLineHeight = fontMetrics.descent - fontMetrics.ascent + fontMetrics.leading;
        return textHeight;
    }

    // 根据资源编号获得线性布局的实际高度（页面来源）
    public static float getRealHeight(Activity act, int resid) {
        LinearLayout llayout = act.findViewById(resid);
        return getRealHeight(llayout);
    }

    // 根据资源编号获得线性布局的实际高度（视图来源）
    public static float getRealHeight(View parent, int resid) {
        LinearLayout llayout = parent.findViewById(resid);
        return getRealHeight(llayout);
    }

    // 计算指定线性布局的实际高度
    public static float getRealHeight(View child) {
        LinearLayout llayout = (LinearLayout) child;
        // 获得线性布局的布局参数
        ViewGroup.LayoutParams params = llayout.getLayoutParams();
        if (params == null) {
            params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        // 获得布局参数里面的宽度规格
        int wdSpec = ViewGroup.getChildMeasureSpec(0, 0, params.width);
        int htSpec = 0;
        if (params.height > 0) { // 高度大于0，说明这是明确的dp数值
            // 按照精确数值的情况计算高度规格
            htSpec = View.MeasureSpec.makeMeasureSpec(params.height, View.MeasureSpec.EXACTLY);
        } else { // MATCH_PARENT=-1，WRAP_CONTENT=-2，所以二者都进入该分支
            htSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        // 重新丈量线性布局的宽高
        llayout.measure(wdSpec, htSpec);
        // 获得并返回线性布局丈量之后的高度
        int height = llayout.getMeasuredHeight();
        // 获得并返回线性布局丈量之后的宽度
        int width = llayout.getMeasuredWidth();
        return height;
    }
}
