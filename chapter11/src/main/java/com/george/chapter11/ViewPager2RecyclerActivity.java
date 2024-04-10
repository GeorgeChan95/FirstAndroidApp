package com.george.chapter11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.george.chapter11.adapter.MobileRecyclerAdapter;
import com.george.chapter11.entity.GoodsInfo;
import com.george.chapter11.util.Utils;

public class ViewPager2RecyclerActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private RadioGroup rg_orientation;
    // ViewPage2视图对象
    private ViewPager2 vp2_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager2_recycler);
        // 单选按钮控件
        rg_orientation = findViewById(R.id.rg_orientation);
        // 设置单选监听
        rg_orientation.setOnCheckedChangeListener(this);
        // 从视图文件中，获取一个ViewPager2视图对象
        vp2_content = findViewById(R.id.vp2_content);
        // 默认设置为水平方向
        vp2_content.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        // 构建适配器
        MobileRecyclerAdapter adapter = new MobileRecyclerAdapter(this, GoodsInfo.getDefaultList());
        // 给ViewPage2设置适配器
        vp2_content.setAdapter(adapter);

        // ViewPager2支持展示左右两页的部分区域(能看到一点点左右两边的内容,非必要)
        RecyclerView cv_content = (RecyclerView) vp2_content.getChildAt(0);
        cv_content.setPadding(Utils.dip2px(this, 60), 0, Utils.dip2px(this, 60), 0);
        // false表示不裁剪下级视图
        cv_content.setClipToPadding(false);

        // ViewPager2支持在翻页时展示切换动画，通过页面转换器计算切换动画的各项参数
//        vp2_content.setPageTransformer(new ViewPager2.PageTransformer() {
//            @Override
//            public void transformPage(@NonNull View page, float position) {
//                // 设置页面的旋转角度
//                page.setRotation(position*360);
//            }
//        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.rb_horizontal) {
            // 设置二代翻页视图的排列方向为水平方向
            vp2_content.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        } else if (checkedId == R.id.rb_vertical) {
            // 设置二代翻页视图的排列方向为垂直方向
            vp2_content.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        }
    }
}