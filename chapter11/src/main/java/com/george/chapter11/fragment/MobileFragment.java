package com.george.chapter11.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.george.chapter11.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MobileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MobileFragment extends Fragment {

    // 声明一个碎片视图
    private View mView;
    // 声明上下文对象
    private Context mContext;
    // 位置序号
    private int position;
    // 图片的资源编号
    private int image_id;
    // 商品的文字描述
    private String mDesc;

    /**
     * 获取碎片实例
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param position 当前碎片的位置
     * @param image_id 图像资源ID
     * @param desc
     * @return
     */
    public static MobileFragment newInstance(int position, int image_id, String desc) {
        MobileFragment fragment = new MobileFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putInt("image_id", image_id);
        bundle.putString("desc", desc);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 获取活动页面的上下文
        mContext = getActivity();
        if (getArguments() != null) {
            position = getArguments().getInt("position", 0);
            image_id = getArguments().getInt("image_id", 0);
            mDesc = getArguments().getString("desc");
        }
        mView = inflater.inflate(R.layout.item_mobile, container, false);
        ImageView iv_pic = mView.findViewById(R.id.iv_pic);
        TextView tv_desc = mView.findViewById(R.id.tv_desc);
        iv_pic.setImageResource(image_id);
        tv_desc.setText(mDesc);

        // Inflate the layout for this fragment
        return mView;
    }
}