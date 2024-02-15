package com.george.logintest.watch;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.george.logintest.R;
import com.george.logintest.util.ViewUtils;

public class HideTextWatcher implements TextWatcher {

    // 监听的上下文
    private Activity activity;
    // 监听的组件
    private EditText editText;
    // 文本最大长度
    private int maxLength;

    public HideTextWatcher(Activity activity, EditText text, int maxLength) {
        this.activity = activity;
        this.editText = text;
        this.maxLength = maxLength;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String text = s.toString();
        // 手机号、密码/验证码达到指定长度后，隐藏系统软键盘
        if ((editText.getId() == R.id.et_password || editText.getId() == R.id.et_phone) && text.length() == maxLength) {
            ViewUtils.hideOnInputMethod(activity, editText);
        }
    }
}
