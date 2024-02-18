package com.george.chapter06;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DatabaseActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_result;
    private String mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        findViewById(R.id.btn_create).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);

        tv_result = findViewById(R.id.tv_result);

        mDatabase = getFilesDir() + "/test.db";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create:
                SQLiteDatabase db = openOrCreateDatabase(getFilesDir() + "/test.db", Context.MODE_PRIVATE, null);
                String desc = String.format("数据库：%s 创建：%s", db.getPath(), (db != null) ? "成功" : "失败");
                tv_result.setText(desc);
                break;
            case R.id.btn_delete:
                boolean flag = deleteDatabase(mDatabase);
                String del_desc = String.format("数据库：%s 删除：%s", mDatabase, flag ? "成功" : "失败");
                tv_result.setText(del_desc);
                break;
        }
    }
}