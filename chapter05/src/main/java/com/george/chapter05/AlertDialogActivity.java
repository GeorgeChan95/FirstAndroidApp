package com.george.chapter05;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AlertDialogActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_dialog);
        findViewById(R.id.btn_uninstall).setOnClickListener(this);
        tv_result = findViewById(R.id.tv_result);
    }

    @Override
    public void onClick(View v) {
        // 创建提醒对话框的建造器
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        // 设置对话框的标题
        dialogBuilder.setTitle("卸载");
        // 设置对话框的内容文本
        dialogBuilder.setMessage("真的打算卸载该应用么?");
        // 设置对话框的图标
        dialogBuilder.setIcon(R.drawable.ic_back);
        // 设置对话框的肯定按钮文本及其点击监听器
        dialogBuilder.setPositiveButton("确定", (dialog, which) -> {
            tv_result.setText("即将卸载该应用");
        });
        // 设置对话框的否定按钮文本及其点击监听器
        dialogBuilder.setNegativeButton("取消", (dialog, which) -> {
            tv_result.setText("暂不卸载，继续使用");
            dialog.cancel();
        });
        //
        dialogBuilder.setNeutralButton("中立", (dialog, which) -> {
           tv_result.setText("");
           dialog.dismiss();
        });

        // 根据建造器构建提醒对话框对象
        AlertDialog dialog = dialogBuilder.create();
        // 显示提醒对话框
        dialog.show();
    }
}