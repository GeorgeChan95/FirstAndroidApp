package com.george.chapter11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.george.chapter11.adapter.GoodsPagerAdapter;
import com.george.chapter11.util.DateUtil;
import com.google.android.material.tabs.TabLayout;

public class TabLayoutActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    // 标签布局对象
    private TabLayout tab_title;
    // 翻页视图对象
    private ViewPager vp_content;
    // 标题文字数组
    private String[] mTitleArray = {"商品", "详情"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);
        // 从布局文件中获取名叫tl_head的工具栏
        Toolbar tl_head = findViewById(R.id.tl_head);
        // 设置工具栏的标题文本为空，不设置默认为项目名
        tl_head.setTitle("");
        // 设置工具栏背景
        tl_head.setBackgroundResource(R.color.blue_light);
        // 设置工具栏左边的导航图表
        tl_head.setNavigationIcon(R.drawable.ic_back);
        // 使用tl_head替换系统自带的ActionBar
        setSupportActionBar(tl_head);

        tab_title = findViewById(R.id.tab_title);
        vp_content = findViewById(R.id.vp_content);

        // 初始化标签布局
        initTabLayout();
        // 初始化翻页视图
        initViewPager();
    }

    /**
     * 初始化ViewPager
     */
    private void initViewPager() {
        GoodsPagerAdapter adapter = new GoodsPagerAdapter(getSupportFragmentManager(), mTitleArray);
        vp_content.setAdapter(adapter);
        vp_content.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                // 翻页视图发生切换，TabLayout也选中对应的标签
                tab_title.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    /**
     * 初始化标签布局 TabLayout
     */
    private void initTabLayout() {
        // 给标签布局添加一个文字标签
        tab_title.addTab(tab_title.newTab().setText(mTitleArray[0]));
        tab_title.addTab(tab_title.newTab().setText(mTitleArray[1]));
        // 给标签布局添加标签选中监听器
        tab_title.addOnTabSelectedListener(this);
        // 监听器ViewPagerOnTabSelectedListener允许直接关联某个翻页视图
//        tab_title.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(vp_content));
    }

    /**
     * 在标签选中时触发
     * @param tab
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        vp_content.setCurrentItem(tab.getPosition());
    }

    /**
     * 在标签取消选中时触发
     * @param tab
     */
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    /**
     * 在标签被重新选中时触发
     * @param tab
     */
    @Override
    public void onTabReselected(TabLayout.Tab tab) {}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 从menu_overflow.xml中构建菜单界面布局
        getMenuInflater().inflate(R.menu.menu_overflow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:// 点击了工具栏左边的返回箭头
                finish();
                break;
            case R.id.menu_refresh:// 点击了刷新图标
                String desc = "当前刷新时间: " + DateUtil.getNowTime();
                Toast.makeText(this, desc, Toast.LENGTH_LONG).show();
                break;
            case R.id.menu_about: // 点击了关于菜单项
                Toast.makeText(this, "这个是工具栏的演示demo", Toast.LENGTH_LONG).show();
                break;
            case R.id.menu_quit:// 点击了退出菜单项
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}