package com.george.chapter08;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.george.chapter08.adapter.PlanetBaseAdapter;
import com.george.chapter08.entity.Planet;
import com.george.chapter08.util.ToastUtil;

import java.util.List;

public class BaseAdapterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner sp_dropdown;
    private List<Planet> planets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_adapter);
        // 下拉选项组件
        sp_dropdown = findViewById(R.id.sp_dropdown);
        // 获取数据集
        planets = Planet.getDefaultList();
        // 基础适配器
        PlanetBaseAdapter adapter = new PlanetBaseAdapter(this, planets);

        sp_dropdown.setPrompt("选择一个星球生活");
        sp_dropdown.setAdapter(adapter);
        sp_dropdown.setSelection(0);
        sp_dropdown.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ToastUtil.show(this, "您选择的是：" + planets.get(position).name);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}