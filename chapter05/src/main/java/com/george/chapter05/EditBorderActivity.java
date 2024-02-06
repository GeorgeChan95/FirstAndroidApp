package com.george.chapter05;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditBorderActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    private EditText password;
    private EditText phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_border);

        // 从布局文件中根据ID获取对应的组件
        password = findViewById(R.id.et_password);
        phone = findViewById(R.id.et_phone);

        // 给控件注册焦点监听事件
        phone.setOnFocusChangeListener(this);
        password.setOnFocusChangeListener(this);

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if ((v.getId() == phone.getId()) && hasFocus) {
            boolean res = verifyPhone(phone.getText().toString());
            if (!res) {
                Toast.makeText(this, "手机号输入异常", Toast.LENGTH_SHORT).show();
            }
        } else if ((v.getId() == password.getId()) && hasFocus) {
            if (password.getText().toString().length() < 6) {
                Toast.makeText(this, "密码不能低于6位", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 校验手机号
     * @param phoneNumber 手机号码
     * @return
     */
    private boolean verifyPhone(String phoneNumber) {
        String regex = "^1[3-9]\\d{9}$"; // 正则表达式
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);
        // 创建匹配器
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}