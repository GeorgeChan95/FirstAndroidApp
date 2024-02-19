package com.george.logintest;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.george.logintest.entity.LoginInfo;
import com.george.logintest.service.LoginService;
import com.george.logintest.util.DBHelperUtil;
import com.george.logintest.util.VerifyUtils;
import com.george.logintest.watch.HideTextWatcher;

import java.util.Random;

public class LoginMainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener, View.OnFocusChangeListener {

    private RadioButton rbPassword;
    private RadioButton rbVerifycode;
    private TextView tvPassword;
    private EditText etPassword;
    private Button btnForget;
    private CheckBox ckRemember;
    private EditText etPhone;
    private Button btnLogin;
    private ActivityResultLauncher<Intent> register;
    private String password = "123456";
    private String verifyCode;
    private SharedPreferences preferences;
    private DBHelperUtil dbHelperUtil;
    private LoginService loginService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);
        // 给RadioGroup登录方式，绑定切换事件
        RadioGroup loginGroup = findViewById(R.id.rg_login);
        loginGroup.setOnCheckedChangeListener(this);
        // 两种登录方式组件
        rbPassword = findViewById(R.id.rb_password);
        rbVerifycode = findViewById(R.id.rb_verifycode);
        // 密码/验证码输入行控件
        tvPassword = findViewById(R.id.tv_password);
        etPassword = findViewById(R.id.et_password);
        btnForget = findViewById(R.id.btn_forget);
        // 记住密码Checkbox
        ckRemember = findViewById(R.id.ck_remember);

        // 给手机号和密码/验证码添加文本变更监听器
        etPhone = findViewById(R.id.et_phone);
        etPhone.addTextChangedListener(new HideTextWatcher(LoginMainActivity.this, etPhone, 11));
        etPassword.addTextChangedListener(new HideTextWatcher(LoginMainActivity.this, etPassword, 6));

        // 忘记密码/发送验证码 添加点击事件
        btnForget.setOnClickListener(this);

        // 登录按钮添加点击事件
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);

        // 给密码输入框添加焦点变更监听器
        etPassword.setOnFocusChangeListener(this);

        // 页面跳转
        register = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            // 从其他页面跳转回当前页面的回调方法
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent intent = result.getData();
                if (intent != null && result.getResultCode() == Activity.RESULT_OK) {
                    // 更新密码
                    String newPassword = intent.getStringExtra("newPassword");
                    password = newPassword;
                    Log.d("Password", "获取到新密码：" + newPassword);
                }
            }
        });

        // 获取SharedPreference
        preferences = getSharedPreferences("config", MODE_PRIVATE);

        // 使用SharedReference回显记住的密码
//        reloadPasswordByPreference();
    }



    @Override
    protected void onStart() {
        super.onStart();
        dbHelperUtil = DBHelperUtil.getInstance(this);
        loginService = new LoginService(dbHelperUtil.openReadLink(), dbHelperUtil.openWriteLink());
        // 使用SQLite回显记住的密码
        reloadPasswrodBySqlite();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dbHelperUtil.closeLink();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            // 选择了密码登录
            case R.id.rb_password:
                tvPassword.setText(R.string.login_password);
                etPassword.setHint(R.string.input_password);
                // inputType设置为密码（以下二选一）
                etPassword.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
//                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnForget.setText(R.string.forget_password);
                ckRemember.setVisibility(View.VISIBLE);
                break;
            // 选择了验证码登录
            case R.id.rb_verifycode:
                tvPassword.setText(R.string.verifycode);
                etPassword.setHint(R.string.input_verifycode);
                etPassword.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
                btnForget.setText(R.string.get_verifycode);
                ckRemember.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        String phone = etPhone.getText().toString();
        // 校验手机号是否符合要求
        if (!VerifyUtils.checkPhoneNum(phone)) {
            // 手机号码编辑框请求焦点，也就是将光标移回请求号码编辑框
            etPhone.requestFocus();
            Toast.makeText(LoginMainActivity.this, "请输入正确的手机号", Toast.LENGTH_LONG).show();
            return;
        }
        switch (v.getId()) {
            case R.id.btn_forget:
                if (rbPassword.isChecked()) { // 如果当前使用密码登录，跳转到忘记密码页面
                    // 携带手机号跳转到忘记密码页面
                    Intent intent = new Intent(this, LoginForgetActivity.class);
                    intent.putExtra("phone", phone);
                    register.launch(intent);
                } else if (rbVerifycode.isChecked()) { // 如果当前使用验证码登录
                    // 生成随机6位验证码
                    verifyCode = String.format("%06d", new Random().nextInt(999999));
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("验证码");
                    builder.setMessage("当前手机号：" + phone + "获取到验证码: " + verifyCode);
                    builder.setPositiveButton("好的", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                break;
            case R.id.btn_login:
                if (rbPassword.isChecked()) { // 密码校验
                    if (!password.equals(etPassword.getText().toString())) {
                        Toast.makeText(this, "登录失败，密码错误", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 登录成功操作
                    loginSuccess();
                } else if (rbVerifycode.isChecked()) {
                    if (!verifyCode.equals(etPassword.getText().toString())) {
                        Toast.makeText(this, "登录失败，验证码错误", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 登录成功操作
                    loginSuccess();
                }
                break;
        }
    }

    /**
     * 焦点变更监听
     * @param v
     * @param hasFocus
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.et_password && hasFocus) {
            String phone = etPhone.getText().toString();
            LoginInfo loginInfo = loginService.queryByPhone(phone);
            if (loginInfo != null) {
                etPhone.setText(loginInfo.getPhone());
                etPassword.setText(loginInfo.getPassword());
                ckRemember.setChecked(true);
            } else {
                etPassword.setText("");
                ckRemember.setChecked(false);
            }
        }
    }

    /**
     * 登录成功
     */
    private void loginSuccess() {
        String desc = String.format("您的手机号码是%s，恭喜你通过登录验证，点击“确定”按钮返回上个页面",
                etPhone.getText().toString());
        // 以下弹出提醒对话框，提示用户登录成功
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("登录成功");
        builder.setMessage(desc);
        builder.setPositiveButton("确定返回", (dialog, which) -> {
            // 结束当前的活动页面
            finish();
        });
        builder.setNegativeButton("我再看看", null);
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();

        // 使用sharedPreference实现记住密码
//        sharedPreferenceRemember();
        // 使用SQLite实现记住密码
        sqliteRemember();
    }

    /**
     * 使用SQLite实现记住密码
     */
    private void sqliteRemember() {
        if (rbPassword.isChecked()) {
            String phone = etPhone.getText().toString();
            String password = etPassword.getText().toString();
            boolean remember = ckRemember.isChecked();

            LoginInfo loginInfo = new LoginInfo(phone, password, remember ? 1 : 0);
            loginService.saveLogin(loginInfo);
        }
    }

    /**
     * 使用sharedReference实现记住密码
     */
    private void sharedPreferenceRemember() {
        if (rbPassword.isChecked()) {
            String phone = etPhone.getText().toString();
            String password = etPassword.getText().toString();
            boolean remember = ckRemember.isChecked();

            // 切换到编辑模式
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString("phone", phone);
            edit.putString("password", password);
            edit.putBoolean("remember", remember);

            // 提交保存的数据
            edit.commit();
        }
    }

    /**
     * 通过SharedPreference加载记住的密码
     */
    private void reloadPasswordByPreference() {
        boolean remember = preferences.getBoolean("remember", false);
        if (remember) {
            String phone = preferences.getString("phone", "");
            String password = preferences.getString("password", "");
            etPhone.setText(phone);
            etPassword.setText(password);
            ckRemember.setChecked(true);
        }
    }

    /**
     * 加载最后登录的用户和密码
     */
    private void reloadPasswrodBySqlite() {
        LoginInfo loginInfo = loginService.queryLastLogin();
        if (loginInfo != null) {
            etPhone.setText(loginInfo.getPhone());
            etPassword.setText(loginInfo.getPassword());
            ckRemember.setChecked(true);
        }
    }
}