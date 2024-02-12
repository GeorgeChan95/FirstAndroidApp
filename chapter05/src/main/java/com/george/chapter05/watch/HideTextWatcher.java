package com.george.chapter05.watch;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.george.chapter05.EditHideActivity;
import com.george.chapter05.util.ViewUtil;

public class HideTextWatcher implements TextWatcher {
    // 声明一个编辑框对象
    private EditText editText;
    // 声明一个最大长度变量
    private int mMaxLength;
    private Activity activity;

    public HideTextWatcher(Activity act, EditText editText, int mMaxLength) {
        this.activity = act;
        this.editText = editText;
        this.mMaxLength = mMaxLength;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        System.out.println("before");
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        System.out.println("onChange");
    }

    /**
     * 在编辑框的输入文本变化后触发
     * @param s
     */
    @Override
    public void afterTextChanged(Editable s) {
        System.out.println("after");
        String  str = s.toString();

        // 当输入的文本长度等于指定的长度时，关闭输入面板
        if (str.length() == mMaxLength) {
            // 隐藏输入法软键盘
            ViewUtil.hideOneInputMethod(activity, editText);
        }

    }
}
