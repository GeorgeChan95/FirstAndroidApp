package com.george.chapter11.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.george.chapter11.R;
import com.george.chapter11.util.ToastUtil;

public class BookCoverFragment extends Fragment implements View.OnClickListener {

    // 上下文对象
    private Context mContext;
    //
    private View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 获取碎片的上下文对象
        mContext = getActivity();
        // 根据布局文件fragment_book_cover.xml生成视图对象
        mView = inflater.inflate(R.layout.fragment_book_cover, container, false);

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ImageView iv_book_cover = mView.findViewById(R.id.iv_book_cover);
        iv_book_cover.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        ToastUtil.show(mContext, "点击的图片");
    }
}
