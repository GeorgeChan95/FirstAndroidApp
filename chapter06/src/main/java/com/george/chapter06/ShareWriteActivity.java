package com.george.chapter06;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class ShareWriteActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_name;
    private EditText et_age;
    private EditText et_height;
    private EditText et_weight;
    private CheckBox ck_married;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_write);

        et_name = findViewById(R.id.et_name);
        et_age = findViewById(R.id.et_age);
        et_height = findViewById(R.id.et_height);
        et_weight = findViewById(R.id.et_weight);
        ck_married = findViewById(R.id.ck_married);

        findViewById(R.id.btn_save).setOnClickListener(this);

        // 获取SharePreference实例，设置文件名和操作模式
        sharedPreferences = getSharedPreferences("share", MODE_PRIVATE);

        // 加载保存的数据
        reloadData();
    }

    /**
     * 从ShareReference中加载数据，并展示到页面中
     */
    private void reloadData() {
        String name = sharedPreferences.getString("name", null);
        if (name != null) {
            et_name.setText(name);
        }
        Integer age = sharedPreferences.getInt("age", 0);
        if (age != 0) {
            et_age.setText(String.valueOf(age));
        }
        Integer height = sharedPreferences.getInt("height", 0);
        if (height != 0) {
            et_height.setText(String.valueOf(height));
        }
        Float weight = sharedPreferences.getFloat("weight", 0f);
        if (weight != 0f) {
            et_weight.setText(String.valueOf(weight));
        }
        boolean married = sharedPreferences.getBoolean("married", false);
        ck_married.setChecked(married);
    }

    @Override
    public void onClick(View v) {
        String name = et_name.getText().toString();
        String age = et_age.getText().toString();
        String height = et_height.getText().toString();
        String weight = et_weight.getText().toString();
        boolean married = ck_married.isChecked();



        // 切换到编辑模式
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("name", name);
        edit.putInt("age", Integer.valueOf(age));
        edit.putInt("height", Integer.valueOf(height));
        edit.putFloat("weight", Float.valueOf(weight));
        edit.putBoolean("married", married);
        // 提交保存的数据
        edit.commit();

        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
    }
}