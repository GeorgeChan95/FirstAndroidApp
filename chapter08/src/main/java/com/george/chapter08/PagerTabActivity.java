package com.george.chapter08;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;

import com.george.chapter08.adapter.ViewPagerAdapter;
import com.george.chapter08.entity.GoodsInfo;
import com.george.chapter08.util.ToastUtil;

import java.util.ArrayList;

public class PagerTabActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ArrayList<GoodsInfo> goodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager_tab);

        ViewPager vp_content = findViewById(R.id.vp_content);
        // 初始化翻页标签栏
        initPagerStrip();
        // 初始化翻页视图
        initViewPager();
    }

    /**
     * 初始书ViewPager组件
     */
    private void initViewPager() {
        // ViewPager组件
        ViewPager vp_content = findViewById(R.id.vp_content);
        // 获取商品数据集
        goodList = GoodsInfo.getDefaultList();
        // ViewPager适配器
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, goodList);
        // 给ViewPager添加适配器
        vp_content.setAdapter(adapter);
        // 设置内容变更监听器
        vp_content.addOnPageChangeListener(this);
        // 设置默认选中的页索引
        vp_content.setCurrentItem(1);

    }

    /**
     * 初始化翻页标签栏
     */
    private void initPagerStrip() {
        // 获取标签栏组件
        PagerTabStrip pts_tab = findViewById(R.id.pts_tab);
        // 设置标题栏文字颜色
        pts_tab.setTextColor(Color.BLACK);
        // 设置标题栏文字大小
        pts_tab.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
    }

    /**
     * 在翻页过程中触发。该方法的三个参数取值说明为 ：第一个参数表示当前页面的序号
     * 第二个参数表示页面偏移的百分比，取值为0到1；第三个参数表示页面的偏移距离
     * @param position
     * @param positionOffset
     * @param positionOffsetPixels
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * 在翻页结束后触发。position表示当前滑到了哪一个页面
     * @param position
     */
    @Override
    public void onPageSelected(int position) {
        ToastUtil.show(this, "您翻到的手机品牌是：" + goodList.get(position).name);
    }

    /**
     * 翻页状态改变时触发。state取值说明为：0表示静止，1表示正在滑动，2表示滑动完毕
     * 在翻页过程中，状态值变化依次为：正在滑动→滑动完毕→静止
     * @param state
     */
    @Override
    public void onPageScrollStateChanged(int state) {

    }
}