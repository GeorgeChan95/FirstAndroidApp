package com.george.logintest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

public class LoginForgetActivity extends AppCompatActivity implements View.OnClickListener {

    // 手机号，参数携带
    private String phone;
    private EditText pwdSecond;
    private EditText pwdFirst;
    // 获取验证码 按钮
    private Button btnVerify;
    // 确定 按钮
    private Button confirm;
    private String verifyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_forget);
        // 从登录页面跳转来，携带的参数
        phone = getIntent().getStringExtra("phone");
        pwdFirst = findViewById(R.id.et_password_first);
        pwdSecond = findViewById(R.id.et_password_second);
        btnVerify = findViewById(R.id.btn_verifycode);
        confirm = findViewById(R.id.btn_confirm);


        // 给‘获取验证码’ 按钮添加点击事件
        btnVerify.setOnClickListener(this);
        // 给'确定' 按钮添加点击事件
        confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_verifycode: // 弹出6位验证码
                verifyCode = String.format("%06d", new Random().nextInt(999999));
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("验证码");
                builder.setMessage("当前手机号：" + phone + " 验证码为：" + verifyCode);
                builder.setPositiveButton("好的", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.btn_confirm: // 校验密码和验证码
                String passwordFirst = pwdFirst.getText().toString();
                String passwordSecond = pwdSecond.getText().toString();
                if (passwordFirst.length() < 6) {
                    Toast.makeText(this, "新密码不能低于6位", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!passwordFirst.equals(passwordSecond)) {
                    Toast.makeText(this, "新密码和旧密码不相同", Toast.LENGTH_LONG).show();
                    return;
                }
                EditText code = findViewById(R.id.et_verifycode);
                if (!code.getText().toString().equals(verifyCode)) {
                    Toast.makeText(this, "验证码错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(this, "密码修改成功", Toast.LENGTH_SHORT).show();
                // 设置新密码作为参数跳转回登录页
                Intent intent = new Intent();
                intent.putExtra("newPassword", passwordFirst);
                setResult(Activity.RESULT_OK, intent);
                // 结束当前页面
                finish();
                break;
        }
    }
}