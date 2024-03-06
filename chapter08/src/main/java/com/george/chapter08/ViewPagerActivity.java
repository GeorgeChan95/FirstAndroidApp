package com.george.chapter08;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.george.chapter08.adapter.ViewPagerAdapter;
import com.george.chapter08.entity.GoodsInfo;
import com.george.chapter08.util.ToastUtil;

import java.util.ArrayList;

public class ViewPagerActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ViewPager vp_content;
    private ArrayList<GoodsInfo> goodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        vp_content = findViewById(R.id.vp_content);
        goodList = GoodsInfo.getDefaultList();
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, goodList);
        vp_content.setAdapter(adapter);
        vp_content.setCurrentItem(3);

        // 给翻页视图添加页面变更监听器
        vp_content.addOnPageChangeListener(this);
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