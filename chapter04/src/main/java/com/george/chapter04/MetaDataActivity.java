package com.george.chapter04;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MetaDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meta_data);
        TextView view = findViewById(R.id.tv_meta);

        // 获取应用包管理器
        PackageManager pm = getPackageManager();
        try {
            // 从应用包管理器中获取当前的活动信息
            ActivityInfo acInfo = pm.getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
            // 获取活动附加的元数据信息
            Bundle metaData = acInfo.metaData;
            // 从包裹中取出名叫 weather 的字符串
            String weather = metaData.get("weather").toString();
            // 在文本视图上显示文字
            view.setText(weather);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}