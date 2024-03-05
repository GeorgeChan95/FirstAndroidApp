package com.george.chapter08;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.george.chapter08.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpinnerIconActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // 定义下拉列表需要显示的行星图标数组
    private static final int[] iconArray = {
            R.drawable.shuixing, R.drawable.jinxing, R.drawable.diqiu,
            R.drawable.huoxing, R.drawable.muxing, R.drawable.tuxing
    };
    // 定义下拉列表需要显示的行星名称数组
    private static final String[] starArray = {"水星", "金星", "地球", "火星", "木星", "土星"};

    private Spinner sp_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinner_icon);
        // 下拉列表组件
        sp_icon = findViewById(R.id.sp_icon);

        // 组装图标和名称，使之一一对应，成一个map
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < iconArray.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("icon", iconArray[i]);
            map.put("name", starArray[i]);
            list.add(map);
        }

        // 构造器生成一个适配器，
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, list, R.layout.item_simple, new String[]{"icon", "name"}, new int[]{R.id.iv_icon, R.id.tv_name});

        sp_icon.setAdapter(simpleAdapter);
        sp_icon.setSelection(0);
        sp_icon.setPrompt("标题"); // 对话框模式才显示
        sp_icon.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ToastUtil.show(this, "选择了：" + starArray[position]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}