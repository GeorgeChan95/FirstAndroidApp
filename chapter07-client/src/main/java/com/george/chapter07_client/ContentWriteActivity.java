package com.george.chapter07_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.george.chapter07_client.entity.UserInfo;
import com.george.chapter07_client.util.ToastUtil;

public class ContentWriteActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "GeorgeTag";

    private EditText et_name;
    private EditText et_age;
    private EditText et_height;
    private EditText et_weight;
    private CheckBox ck_married;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_write);

        et_name = findViewById(R.id.et_name);
        et_age = findViewById(R.id.et_age);
        et_height = findViewById(R.id.et_height);
        et_weight = findViewById(R.id.et_weight);
        ck_married = findViewById(R.id.ck_married);

        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.btn_read).setOnClickListener(this);
        findViewById(R.id.btn_update).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String name = et_name.getText().toString();
        String age = et_age.getText().toString();
        String height = et_height.getText().toString();
        String weight = et_weight.getText().toString();
        int married = ck_married.isChecked() ? 1 : 0;

        switch (v.getId()) {
            case R.id.btn_save:
                ContentValues values = new ContentValues();
                values.put(UserInfoContent.USER_NAME, name);
                values.put(UserInfoContent.USER_AGE, age);
                values.put(UserInfoContent.USER_HEIGHT, height);
                values.put(UserInfoContent.USER_WEIGHT, weight);
                values.put(UserInfoContent.USER_MARRIED, married);
                Uri insertUri = getContentResolver().insert(UserInfoContent.CONTENT_URI, values);
                ToastUtil.show(this, "保存成功");
                break;
            case R.id.btn_delete:
                // 方式一：根据输入的name属性值批量删除数据
//                int row = getContentResolver().delete(UserInfoContent.CONTENT_URI, "name=?", new String[]{name});
//                if (row > 0) {
//                    ToastUtil.show(this, "删除成功");
//                }
                // 方式二：根据id，单个删除数据
                Uri uri = ContentUris.withAppendedId(UserInfoContent.CONTENT_URI, 2);
                int row = getContentResolver().delete(uri, null, null);
                if (row > 0) {
                    ToastUtil.show(this, "删除成功");
                }
                break;
            case R.id.btn_read:
                Cursor cursor = getContentResolver().query(UserInfoContent.CONTENT_URI, null, "age>=?", new String[]{age}, UserInfoContent.USER_AGE);
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(0);
                    String name1 = cursor.getString(1);
                    Integer age1 = cursor.getInt(2);
                    Long height1 = cursor.getLong(3);
                    Float weight1 = cursor.getFloat(4);
                    boolean married1 = cursor.getInt(5) > 0 ? true : false;
                    UserInfo userInfo = new UserInfo(id, name1, age1, height1, weight1, married1);
                    Log.d(TAG, "获取到用户信息：" + userInfo);
                }
                // 关闭数据库游标
                cursor.close();
                break;
            case R.id.btn_update:
                ContentValues update = new ContentValues();
                update.put(UserInfoContent.USER_NAME, name);
                update.put(UserInfoContent.USER_AGE, age);
                update.put(UserInfoContent.USER_HEIGHT, height);
                update.put(UserInfoContent.USER_WEIGHT, weight);
                update.put(UserInfoContent.USER_MARRIED, married);
                int updateRow = getContentResolver().update(UserInfoContent.CONTENT_URI, update, "name=?", new String[]{name});
                if (updateRow > 0) {
                    ToastUtil.show(this, "更新成功");
                }
                break;
        }
    }
}