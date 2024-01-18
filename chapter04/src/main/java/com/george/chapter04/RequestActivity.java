package com.george.chapter04;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.george.chapter04.utils.DateUtils;

public class RequestActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView sendTextView;
    private TextView sendRecView;
    private int sendTextCode = 0;
    private ActivityResultLauncher<Intent> register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        sendTextView = findViewById(R.id.tv_send_text);
        sendRecView = findViewById(R.id.tv_send_rec);

        findViewById(R.id.btn_send).setOnClickListener(this);

        register = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> { // 返回的数据参数
            if (result != null) {
                // 返回结果中的，携带参数的意图
                Intent intent = result.getData();
                int resultCode = result.getResultCode();
                if (intent != null && resultCode == Activity.RESULT_OK) {
                    // 获取具体的参数
                    Bundle bundle = intent.getExtras();
                    String receive_text = bundle.get("receive_text").toString();
                    String receive_time = bundle.get("receive_time").toString();

                    // 刷新原有页面的内容
                    String desc = String.format("收到返回消息：\n应答时间为: %s\n应答内容为:%s", receive_time, receive_text);
                    sendRecView.setText(desc);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        // 创建一个意图对象，并指定要跳转的页面
        Intent intent = new Intent(RequestActivity.this, ResponseActivity.class);
        // 创建一个新的包裹
        Bundle bundle = new Bundle();
        // 获取要发送的参数信息
        String text = sendTextView.getText().toString();
        String nowTime = DateUtils.getNowTime();
        // 将要发送的参数信息放到包裹中
        bundle.putString("send_text", text);
        bundle.putString("send_time", nowTime);
        // 给意图设置打开活动页面的方式
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // 给意图添加参数
        intent.putExtras(bundle);
        // 打开新的activity页面，并等待结果返回 (该方法已过时)
//        startActivityForResult(intent, sendTextCode);

        // 跳转新页面
        register.launch(intent);
    }
}