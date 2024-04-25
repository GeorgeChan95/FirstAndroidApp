package com.george.chapter13;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.george.chapter13.entity.LoginInfo;
import com.george.chapter13.entity.UserInfo;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rxhttp.RxHttp;

public class OkhttpCallActivity extends AppCompatActivity {

    private LinearLayout ll_login; // 声明一个线性布局对象
    private EditText et_username; // 声明一个编辑框对象
    private EditText et_password; // 声明一个编辑框对象
    private TextView tv_result; // 声明一个文本视图对象
    private int mCheckedId; // 当前选中的单选按钮资源编号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okhttp_call);
        ll_login = findViewById(R.id.ll_login);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        tv_result = findViewById(R.id.tv_result);

        mCheckedId = R.id.rb_get;

        RadioGroup rg_method = findViewById(R.id.rg_method);
        // 切换请求方式，隐藏、显示输入框
        rg_method.setOnCheckedChangeListener((group, checkedId) -> {
            mCheckedId = checkedId;
            int visibility = mCheckedId == R.id.rb_get ? GONE : VISIBLE;
            ll_login.setVisibility(visibility);
        });

        findViewById(R.id.btn_send).setOnClickListener(v -> {
            if (mCheckedId == R.id.rb_get) { // GET 请求
//                httpGet();
                rxHttpGet();
            } else if (mCheckedId == R.id.rb_post_form) { // POST请求发送FORM表单
//                postForm();
                rxHttpPostForm();
            } else if (mCheckedId == R.id.rb_post_json) { // POST请求发送JSON数据
//                postJson();
                rxHttpPostJson();
            }
        });
    }

    /**
     * Http发送Get请求
     */
    private void httpGet() {
        // 创建HttpClient客户端对象
        OkHttpClient client = new OkHttpClient();
        // 创建GET请求结构
        Request request = new Request.Builder()
                .get() // 因为OkHttp默认采用get方式，所以这里可以不调get方法
                .header("Accept", "*/*") // 给http请求添加头部信息
                .header("Accept-Encoding", "gzip, deflate, br, zstd")
                .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .url("http://192.168.6.209:8008/user/getById/101") // 指定http请求的调用地址
                .build();
        // 根据请求结构调用对象
        Call call = client.newCall(request);
        // 加入HTTP请求队列。异步调用，并设置接口应答的回调方法
        call.enqueue(new Callback() {
            /**
             * 请求失败回调
             * @param call
             * @param e
             */
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> tv_result.setText("GET请求获取用户信息异常，" + e.getMessage()));
            }

            /**
             * 请求成功回调
             * @param call
             * @param response
             * @throws IOException
             */
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String string = response.body().string();
                Gson gson = new Gson();
                UserInfo userInfo = gson.fromJson(string, UserInfo.class);
                runOnUiThread(() -> tv_result.setText("GET请求获取用户信息：" + userInfo));
            }
        });
    }

    /**
     * 表单提交POST请求
     */
    private void postForm() {
        String username = et_username.getText().toString();
        String password = this.et_password.getText().toString();
        // 创建表单对象
        FormBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();
        // 创建okhttp客户端对象
        OkHttpClient client = new OkHttpClient();
        // 创建POST请求结构
        Request request = new Request.Builder()
                .post(formBody)
                .header("Accept", "*/*") // 给http请求添加头部信息
                .header("Accept-Encoding", "gzip, deflate, br, zstd")
                .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .url("http://192.168.6.209:8008/user/login")
                .build();
        // 根据请求结构创建调用对象
        Call call = client.newCall(request);
        // 加入HTTP请求队列。异步调用，并设置接口应答的回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    tv_result.setText("调用登录接口报错：" + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res = response.body().string();
                runOnUiThread(() -> tv_result.setText("调用登录接口返回：" + res));
            }
        });
    }

    /**
     * Http Post请求提交json数据
     */
    private void postJson() {
        String username = et_username.getText().toString();
        String password = this.et_password.getText().toString();
        LoginInfo login = new LoginInfo(username, password);
        Gson gson = new Gson();
        String json = gson.toJson(login);

        // 创建okhttp客户端对象
        OkHttpClient client = new OkHttpClient();
        // 创建POST请求体
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json;charset=UTF-8"));
        Request request = new Request.Builder()
                .post(body)
                .url("http://192.168.6.209:8008/user/loginJson")
                .build();
        // 根据请求结构创建调用对象
        Call call = client.newCall(request);
        // 加入HTTP请求队列。异步调用，并设置接口应答的回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // 回到主线程操纵界面
                runOnUiThread(() -> tv_result.setText("调用登录接口报错：" + e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res = response.body().string();
                runOnUiThread(() -> tv_result.setText("调用登录接口返回：" + res));
            }
        });
    }

    /**
     * 使用RxHttp实现Get请求
     */
    private void rxHttpGet() {
        RxHttp.get("user/getById/101")
                .toObservableString()
                .subscribe(str -> {
                    Gson gson = new Gson();
                    UserInfo userInfo = gson.fromJson(str, UserInfo.class);
                    runOnUiThread(() -> tv_result.setText("RxHttp GET请求获取用户信息：" + userInfo));
                }, throwable -> {
                    runOnUiThread(() -> tv_result.setText("RxHttp GET请求获取用户信息异常，" + throwable.getMessage()));
                });
    }

    /**
     * 使用RxHttp 提交Form表单
     */
    private void rxHttpPostForm() {
        String username = et_username.getText().toString();
        String password = this.et_password.getText().toString();
        RxHttp.postForm("user/login")
                .add("username", username)
                .add("password", password)
                .toObservableString()
                .subscribe(str -> {
                    runOnUiThread(() -> tv_result.setText("调用登录接口返回：" + str));
                }, throwable -> {
                    tv_result.setText("调用登录接口报错：" + throwable.getMessage());
                });
    }

    /**
     * 使用RxHttp 提交JSON数据
     */
    private void rxHttpPostJson() {
        String username = et_username.getText().toString();
        String password = this.et_password.getText().toString();
        LoginInfo login = new LoginInfo(username, password);
        Gson gson = new Gson();
        String json = gson.toJson(login);

        RxHttp.postJson("user/loginJson")
                .addAll(json)
                .toObservableString()
                .subscribe(str -> {
                    runOnUiThread(() -> tv_result.setText("调用登录接口返回：" + str));
                }, throwable -> {
                    tv_result.setText("调用登录接口报错：" + throwable.getMessage());
                });
    }
}