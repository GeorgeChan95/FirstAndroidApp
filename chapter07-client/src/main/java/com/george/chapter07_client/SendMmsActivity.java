package com.george.chapter07_client;

import static android.content.Intent.EXTRA_STREAM;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.george.chapter07_client.util.ToastUtil;

public class SendMmsActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "GeorgeTag";

    private ImageView iv_appendix;
    private EditText et_phone;
    private EditText et_title;
    private EditText et_message;
    // 选择图片的跳转注册监听
    private ActivityResultLauncher<Intent> resultLauncher;
    // 选择的图片的URI
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_mms);
        et_phone = findViewById(R.id.et_phone);
        et_title = findViewById(R.id.et_title);
        et_message = findViewById(R.id.et_message);
        iv_appendix = findViewById(R.id.iv_appendix);
        // 给添加图片按钮，设置点击监听事件
        iv_appendix.setOnClickListener(this);
        findViewById(R.id.btn_send_mms).setOnClickListener(this);
        // 跳转到相册，选择图片后返回，监听返回的参数
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int resultCode = result.getResultCode();
                if (resultCode == RESULT_OK) {
                    Intent intent = result.getData();
                    // 选择的图片的访问路径
                    imageUri = intent.getData();
                    if (imageUri != null) {
                        Log.d(TAG, "选择图片的URI：" + imageUri.toString());
                        iv_appendix.setImageURI(imageUri);
                    }
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_appendix: // 添加图片按钮
                // 跳转到系统相册，选择图片，并返回
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                // 打开系统相册，并等待图片选择结果
                resultLauncher.launch(intent);
                break;
            case R.id.btn_send_mms: // 信息发送按钮
                sendMms(et_phone.getText().toString(), et_title.getText().toString(), et_message.getText().toString());
                break;
        }
    }

    /**
     * 发送彩信
     * @param phone
     * @param title
     * @param message
     */
    private void sendMms(String phone, String title, String message) {
        // 创建一个发送动作的意图
        Intent intent = new Intent(Intent.ACTION_SEND);
        // 另外开启新页面
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Intent 的接受者将被准许读取Intent 携带的URI数据
        intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
        // 彩信发送的目标号码
        intent.putExtra("address", phone);
        // 彩信的标题
        intent.putExtra("subject", title);
        // 彩信的内容
        intent.putExtra("sms_body", message);
        // 彩信图片附件
        intent.putExtra(EXTRA_STREAM, imageUri);
        // 附件类型
        intent.setType("image/*");
        // 因为未指定要打开哪个页面，所以系统会在底部弹出选择窗口
        startActivity(intent);
        ToastUtil.show(this, "请在弹窗中选择短信或者信息应用");
    }
}