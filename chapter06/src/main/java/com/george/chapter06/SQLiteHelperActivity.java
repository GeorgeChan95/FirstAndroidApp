package com.george.chapter06;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.george.chapter06.entity.UserInfo;
import com.george.chapter06.service.UserService;
import com.george.chapter06.util.DBHelperUtil;
import com.george.chapter06.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelperActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_name;
    private EditText et_age;
    private EditText et_height;
    private EditText et_weight;
    private CheckBox ck_married;
    // DBHelper实例
    private DBHelperUtil dbHelper;
    private SQLiteDatabase readLink;
    private SQLiteDatabase writeLink;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite_helper);

        et_name = findViewById(R.id.et_name);
        et_age = findViewById(R.id.et_age);
        et_height = findViewById(R.id.et_height);
        et_weight = findViewById(R.id.et_weight);
        ck_married = findViewById(R.id.ck_married);

        // 监听 添加、删除、修改、查询按钮的点击事件
        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.btn_update).setOnClickListener(this);
        findViewById(R.id.btn_query).setOnClickListener(this);
    }

    /**
     * 页面启动后执行
     */
    @Override
    protected void onStart() {
        super.onStart();
        // 获得数据库帮助器的实例
        dbHelper = DBHelperUtil.getInstance(this);
        // 打开数据库帮助器的读写连接
        readLink = dbHelper.openReadLink();
        writeLink = dbHelper.openWriteLink();
    }

    /**
     * 页面退出后执行
     */
    @Override
    protected void onStop() {
        super.onStop();
        // 关闭数据库连接
        dbHelper.closeLink();
    }

    @Override
    public void onClick(View v) {
        String name = et_name.getText().toString();
        String age = et_age.getText().toString();
        String height = et_height.getText().toString();
        String weight = et_weight.getText().toString();
        boolean married = ck_married.isChecked();
        UserInfo userInfo = null;
        List<String> params = new ArrayList<>();
        StringBuffer condition = new StringBuffer("1=1");

        switch (v.getId()) {
            case R.id.btn_save:
                userInfo = new UserInfo();
                if (name != null) userInfo.setName(name);
                if (age != null && !age.equals("")) userInfo.setAge(Integer.valueOf(age));
                if (height != null && !height.equals("")) userInfo.setHeight(Long.valueOf(height));
                if (weight != null && !weight.equals("")) userInfo.setWeight(Float.valueOf(weight));
                userInfo.setMarried(married);
                userService = getUserServiceInstance();
                long row = userService.insert(userInfo);
                if (row > 0) {
                    ToastUtil.show(this, "操作成功");
                }
                break;
            case R.id.btn_delete:
                params = new ArrayList<>();
                condition = new StringBuffer("1=1");
                if (name != null && !name.equals("")) {
                    condition.append(" and name=?");
                    params.add(name);
                }
                if (age != null && !age.equals("")) {
                    condition.append(" and age=?");
                    params.add(age);
                }
                if (params.size() <= 0) {
                    ToastUtil.show(this, "姓名或者年龄不能都为空");
                    return;
                }
                String[] args = params.toArray(new String[params.size()]);
                userService = getUserServiceInstance();
                row = userService.delete(condition.toString(), args);
                if (row > 0) {
                    ToastUtil.show(this, "删除操作成功");
                }
                break;
            case R.id.btn_update:
                userInfo = new UserInfo();
                if (name == null || name.equals("")) {
                    ToastUtil.show(this, "姓名不能为空");
                    return;
                }
                params = new ArrayList<>();
                condition = new StringBuffer("1=1");
                if (name != null && !name.equals("")) {
                    userInfo.setName(name);
                    params.add(name);
                    condition.append(" and name=?");
                }
                if (age != null && !age.equals("")) {
                    userInfo.setAge(Integer.valueOf(age));
                    params.add(age);
                    condition.append(" and age=?");
                }
                if (height != null && !height.equals("")) userInfo.setHeight(Long.valueOf(height));
                if (weight != null && !weight.equals("")) userInfo.setWeight(Float.valueOf(weight));
                userInfo.setMarried(married);
                String[] updateArgs = params.toArray(new String[params.size()]);
                userService = getUserServiceInstance();
                row = userService.update(userInfo, condition.toString(), updateArgs);
                if (row > 0) {
                    ToastUtil.show(this, "更新操作成功");
                }
                break;
            case R.id.btn_query:
                params = new ArrayList<>();
                condition = new StringBuffer("1=1");
                if (name != null && !name.equals("")) {
                    params.add(name);
                    condition.append(" and name=?");
                }
                if (age != null && !age.equals("")) {
                    params.add(age);
                    condition.append(" and age=?");
                }
                userService = getUserServiceInstance();
                String[] queryArgs = params.toArray(new String[params.size()]);
                List<UserInfo> userList = userService.query(null, condition.toString(), queryArgs, null, null, "age");
                if (userList.size() > 0) {
                    for (UserInfo info : userList) {
                        System.out.println(info.toString());
                    }
                    UserInfo user = userList.get(0);
                    et_age.setText(user.getAge().toString());
                    et_height.setText(user.getHeight().toString());
                    et_weight.setText(user.getWeight().toString());
                    ck_married.setChecked(user.getMarried());
                }
                break;
        }
    }

    /**
     * 获取UserService实例
     * @return
     */
    private UserService getUserServiceInstance() {
        if (userService == null) {
            userService = new UserService(readLink, writeLink);
        }
        return userService;
    }
}