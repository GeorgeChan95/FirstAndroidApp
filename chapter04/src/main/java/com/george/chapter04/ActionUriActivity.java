package com.george.chapter04;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class ActionUriActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_uri);
        findViewById(R.id.btn_dial).setOnClickListener(this);
        findViewById(R.id.btn_sms).setOnClickListener(this);
        findViewById(R.id.btn_my).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        String phoneNo = "12345";

        switch (v.getId()) {
            case R.id.btn_dial:
                // 设置意图动作为准备拨号
                intent.setAction(Intent.ACTION_DIAL);
                // 声明一个拨号的Uri
                Uri uri = Uri.parse("tel:" + phoneNo);
                // 设置意图前往的路径
                intent.setData(uri);
                startActivity(intent);
                break;
            case R.id.btn_sms:
                // 设置意图动作为发短信
                intent.setAction(Intent.ACTION_SENDTO);
                // 声明一个发送短信的Uri
                Uri uri23 = Uri.parse("smsto:+8618326088610");
//                Uri uri2 = Uri.parse("mailto:" + "george_95@126.com");
                intent.setData(uri23);
                startActivity(intent);
                break;
            case R.id.btn_my:
                // 定义在 chapter03/src/main/AndroidManifest.xml
                intent.setAction("android.intent.action.GEORGE");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}