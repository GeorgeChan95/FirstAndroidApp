package com.george.chapter08.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.george.chapter08.R;
import com.george.chapter08.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 启动引导页适配器
 */
public class LaunchSimpleAdapter extends PagerAdapter {
    private Context mContext;
    private int[] imageList;

    List<View> views = new ArrayList<>();

    public LaunchSimpleAdapter(Context mContext, int[] imageList) {
        this.mContext = mContext;
        this.imageList = imageList;

        for (int i = 0; i < imageList.length; i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_lanuch, null);
            ImageView iv_lanuch = view.findViewById(R.id.iv_lanuch);
            RadioGroup rg_indicate = view.findViewById(R.id.rg_indicate);
            Button btn_start = view.findViewById(R.id.btn_start);

            iv_lanuch.setImageResource(imageList[i]);

            // 每个页面都分配一组对应的单选按钮
            for (int j = 0; j < imageList.length; j++) {
                RadioButton radio = new RadioButton(mContext);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                radio.setLayoutParams(params);
                radio.setPadding(10, 10, 10, 10);
                rg_indicate.addView(radio);
            }
            // 与页面下标索引相同的Radio，设置选中状态
            ((RadioButton) rg_indicate.getChildAt(i)).setChecked(true);

            // 如果是最后一个页面，显示按钮
            if (i == imageList.length - 1) {
                btn_start.setVisibility(View.VISIBLE);
                btn_start.setOnClickListener(v -> ToastUtil.show(mContext, "即将打开应用"));
            }

            views.add(view);
        }

    }

    /**
     * 返回ViewPager加载的Item数量
     * @return
     */
    @Override
    public int getCount() {
        return imageList.length;
    }

    /**
     * 实例化指定位置的页面，并将其添加到容器中
     *
     * 添加一个view到container中，而后返回一个跟这个view可以关联起来的对象，
     * 这个对象能够是view自身，也能够是其余对象，
     * 关键是在isViewFromObject可以将view和这个object关联起来
     * @param container
     * @param position
     * @return
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = views.get(position);
        container.addView(view);
        return view;
    }

    /**
     * 从容器中销毁指定位置的页面
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = views.get(position);
        container.removeView(view);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
