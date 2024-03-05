package com.george.chapter08;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.george.chapter08.adapter.PlanetListWithButtonAdapter;
import com.george.chapter08.entity.Planet;
import com.george.chapter08.util.ToastUtil;

import java.util.List;

/**
 * 列表项点击时，有子元素，点击事件会向下传递
 */
public class ListFocusActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView lv_planet;
    private List<Planet> planets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_focus);

        lv_planet = findViewById(R.id.lv_planet);
        // 获取数据集
        planets = Planet.getDefaultList();
        PlanetListWithButtonAdapter adapter = new PlanetListWithButtonAdapter(this, planets);
        lv_planet.setAdapter(adapter);
//        lv_planet.setSelector(0);
        // 列表点击事件
        lv_planet.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ToastUtil.show(this, "点击了列表，选项为：" + planets.get(position));
    }
}