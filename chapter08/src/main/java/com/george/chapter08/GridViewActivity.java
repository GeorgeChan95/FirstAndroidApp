package com.george.chapter08;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.george.chapter08.adapter.PlanetGridAdapter;
import com.george.chapter08.entity.Planet;
import com.george.chapter08.util.ToastUtil;

import java.util.List;

public class GridViewActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private GridView gv_planet;
    private List<Planet> planetList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);
        gv_planet = findViewById(R.id.gv_planet);
        planetList = Planet.getDefaultList();
        PlanetGridAdapter adapter = new PlanetGridAdapter(this, planetList);

        gv_planet.setAdapter(adapter);
        // 拉伸模式
        // 若有剩余空间，则拉伸列宽挤掉空隙
        gv_planet.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        // 不拉伸
//        gv_planet.setStretchMode(GridView.NO_STRETCH);
        // 若有剩余空间，列宽不变，空间分配到每列间的空隙
//        gv_planet.setStretchMode(GridView.STRETCH_SPACING);
        gv_planet.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ToastUtil.show(this, "您选择了：" + planetList.get(position).name);
    }
}