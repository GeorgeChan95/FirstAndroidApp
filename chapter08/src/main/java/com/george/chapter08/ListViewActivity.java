package com.george.chapter08;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.george.chapter08.adapter.PlanetBaseAdapter;
import com.george.chapter08.entity.Planet;
import com.george.chapter08.util.ToastUtil;
import com.george.chapter08.util.Utils;

import java.util.List;

public class ListViewActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, CompoundButton.OnCheckedChangeListener {

    // ListView组件
    private ListView lv_planet;
    // 行星数据集
    private List<Planet> planets;
    // 分割线显示
    private CheckBox ck_divider;
    // 按压背景显示
    private CheckBox ck_selector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        // 获取ListView组件
        lv_planet = findViewById(R.id.lv_planet);

        // 获取展示的数据集
        planets = Planet.getDefaultList();
        // 使用基础适配器，在适配器中使用了 item_list.xml 布局文件
        PlanetBaseAdapter adapter = new PlanetBaseAdapter(this, planets);

        // 给ListView组件设置适配器
        lv_planet.setAdapter(adapter);
        // 设置分割线高度
        lv_planet.setDividerHeight(10);
        // 给ListView元素设置点击事件
        lv_planet.setOnItemClickListener(this);
        // 给ListView元素设置长按事件
        lv_planet.setOnItemLongClickListener(this);

        ck_divider = findViewById(R.id.ck_divider);
        ck_divider.setOnCheckedChangeListener(this);
        ck_selector = findViewById(R.id.ck_selector);
        ck_selector.setOnCheckedChangeListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ToastUtil.show(this, "点击了：" + planets.get(position).name);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ToastUtil.show(this, "长按了：" + planets.get(position).name);
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.ck_divider:
                if (isChecked) {
                    Drawable drawable = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        drawable = getResources().getDrawable(R.color.black, getTheme());
                    } else {
                        drawable = getResources().getDrawable(R.color.black);
                    }
                    // 设置分割线
                    lv_planet.setDivider(drawable);
                    // 设置分割线的高
                    lv_planet.setDividerHeight(Utils.dip2px(this, 1));
                } else {
                    lv_planet.setDivider(null);
                    lv_planet.setDividerHeight(0);
                }
                break;
            case R.id.ck_selector:
                if (isChecked) {
                    // 使用自定义背景组件
                    Drawable drawable = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        drawable = getResources().getDrawable(R.drawable.list_selector, getTheme());
                    } else {
                        drawable = getResources().getDrawable(R.drawable.list_selector);
                    }
                    // 设置按压背景
                    lv_planet.setSelector(drawable);
                } else {
                    // 设置按压背景
                    lv_planet.setSelector(R.color.transparent);
                }
                break;
        }
    }
}