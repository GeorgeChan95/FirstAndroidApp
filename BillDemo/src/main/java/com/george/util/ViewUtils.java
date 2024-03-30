package com.george.util;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class ViewUtils {
    /**
     * 隐藏软键盘
     * @param activity 上下文页面
     * @param editText 组件
     */
    public static void hideOnInputMethod(Activity activity, EditText editText) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
