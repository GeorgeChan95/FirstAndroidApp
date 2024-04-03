package com.george.chapter10.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

import com.george.chapter10.R;


@SuppressLint("AppCompatCustomView")
public class CustomButton extends Button {
    private static final String TAG = "GeorgeTag";

    public CustomButton(Context context) {
        super(context);
        Log.d(TAG, "只有一个输入参数");
    }

    public CustomButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.customButtonStyle);
        Log.d(TAG, "只有两个输入参数");
    }

    /**
     * style属性 > defStyleAttr > defStyleRes
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.CommonButton);
        Log.d(TAG, "只有三个输入参数");
    }

    /**
     * style属性 > defStyleAttr > defStyleRes
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    @SuppressLint("NewApi")
    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Log.d(TAG, "只有四个输入参数");
    }
}
