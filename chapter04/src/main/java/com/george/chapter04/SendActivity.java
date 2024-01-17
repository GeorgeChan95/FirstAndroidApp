package com.george.chapter04;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.george.chapter04.utils.DateUtils;

public class SendActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        tv_send = findViewById(R.id.tv_send);
        findViewById(R.id.btn_send).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, ReceiveActivity.class);
        // 创建一个新包裹
        Bundle bundle = new Bundle();
        bundle.putString("request_time", DateUtils.getNowTime());
        bundle.putString("request_content", tv_send.getText().toString());
        intent.putExtras(bundle);
        startActivity(intent);
    }
}