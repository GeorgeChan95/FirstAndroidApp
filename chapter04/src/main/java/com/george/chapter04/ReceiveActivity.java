package com.george.chapter04;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ReceiveActivity extends AppCompatActivity {

    private TextView tv_receive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        // 从布局文件中获取名为tv_receive的文本视图
        tv_receive = findViewById(R.id.tv_receive);
        // 从上一个页面传来的意图中获取快递包裹
        Bundle bundle = getIntent().getExtras();
        // 从包裹中取出名为request_time的字符串
        String request_time = bundle.getString("request_time");
        // 从包裹中取出名为request_content的字符串
        String request_content = bundle.getString("request_content");
        String desc = String.format("收到请求消息：\n请求时间为: %s\n请求内容为: %s", request_time, request_content);
        tv_receive.setText(desc);
    }
}