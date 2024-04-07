package com.george.chapter10;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.george.chapter10.adapter.PlanetListAdapter;
import com.george.chapter10.entity.Planet;
import com.george.chapter10.widget.NoScrollListView;

public class NoscrollListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noscroll_list);

        PlanetListAdapter adapter1 = new PlanetListAdapter(this, Planet.getDefaultList());
        // 从布局文件中获取名叫lv_planet的列表视图
        // lv_planet是系统自带的ListView，被ScrollView嵌套只能显示一行
        ListView lv_planet = findViewById(R.id.lv_planet);
        lv_planet.setAdapter(adapter1);

        PlanetListAdapter adapter2 = new PlanetListAdapter(this, Planet.getDefaultList());
        NoScrollListView nslv_planet = findViewById(R.id.nslv_planet);
        nslv_planet.setAdapter(adapter2);
    }
}