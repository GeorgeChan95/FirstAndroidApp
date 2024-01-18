package com.george.chapter04;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.george.chapter04.utils.DateUtils;

public class ResponseActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);
        TextView recText = findViewById(R.id.rec_text);
        // 获取上个页面发来的参数
        Bundle bundle = getIntent().getExtras();
        String send_text = bundle.get("send_text").toString();
        String send_time = bundle.get("send_time").toString();
        // 刷新原有页面的内容
        String desc = String.format("第二页面收到消息：\n发送时间为：%s\n收到内容为：%s", send_time, send_text);
        recText.setText(desc);

        findViewById(R.id.btn_rec).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(ResponseActivity.this, RequestActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("receive_time", DateUtils.getNowTime());
        bundle.putString("receive_text", "我又回来了");
        intent.putExtras(bundle);

        // 携带意图返回上一个页面。RESULT_OK表示处理成功
        setResult(Activity.RESULT_OK, intent);
        // 销毁当前页面，回到上一页面
        finish();
    }
}