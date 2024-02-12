package com.george.chapter05;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.george.chapter05.util.VerifyUtils;

/**
 * 焦点变更监听器
 */
public class EditFocusActivity2 extends AppCompatActivity implements View.OnFocusChangeListener {

    private EditText et_phone;
    private EditText et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_focus2);

        et_phone = findViewById(R.id.et_phone);
        et_password = findViewById(R.id.et_password);

        et_phone.setOnFocusChangeListener(this);
        et_password.setOnFocusChangeListener(this);

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // 校验手机号
        if (v.getId() == et_phone.getId() && hasFocus) {
            String phoneNumber = et_phone.getText().toString();
            boolean res = VerifyUtils.checkPhoneNum(phoneNumber);
            if (!res) {
                // 手机号码编辑框请求焦点，也就是将光标移回请求号码编辑框
                et_phone.requestFocus();
                Toast.makeText(this, "请输入正确的11位手机号", Toast.LENGTH_LONG).show();
                return;
            }
        }

        // 校验密码
        if (v.getId() == et_password.getId() && hasFocus) {
            String password = et_password.getText().toString();
            if (password.length() < 6 && password.length() > 0) {
                et_password.requestFocus();
                Toast.makeText(this, "密码不能低于6位", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }
}