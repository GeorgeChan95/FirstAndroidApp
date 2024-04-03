package com.george.chapter10.util;

import android.graphics.Paint;
import android.text.TextUtils;

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
}
