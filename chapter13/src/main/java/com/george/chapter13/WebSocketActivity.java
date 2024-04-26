package com.george.chapter13;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.george.chapter13.listener.WsListener;
import com.george.chapter13.listener.WsMsgRespListener;
import com.george.chapter13.websocket.WsManager;

public class WebSocketActivity extends AppCompatActivity implements WsMsgRespListener {
    private EditText et_input; // 声明一个编辑框对象
    private TextView tv_response; // 声明一个文本视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_socket);
        et_input = findViewById(R.id.et_input);
        tv_response = findViewById(R.id.tv_response);

        // 初始化Websocket连接
        WsManager wsManger = WsManager.getWsManger();
        WsListener listener = new WsListener(this, this);
        wsManger.initWebSocket(this, listener);

        findViewById(R.id.btn_send).setOnClickListener(v -> {
            String content = et_input.getText().toString();
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(this, "请输入消息文本", Toast.LENGTH_SHORT).show();
                return;
            }
            new Thread(() -> wsManger.sendTextMessage(content)).start(); // 启动线程发送文本消息
        });

    }

    @Override
    public void receiveResponse(String msg) {
        tv_response.setText(msg);
    }
}