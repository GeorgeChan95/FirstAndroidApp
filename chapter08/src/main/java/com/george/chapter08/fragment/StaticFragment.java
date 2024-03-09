package com.george.chapter08.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.george.chapter08.R;

public class StaticFragment extends Fragment {
    private static final String TAG = "GeorgeTag";

    /**
     * 把碎片贴到页面上
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "Fragment onAttach");
    }

    /**
     * 页面创建
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "Fragment onCreate");
        super.onCreate(savedInstanceState);
    }

    /**
     * 加载 活动视图文件
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "Fragment onCreateView");
        View view = inflater.inflate(R.layout.fragment_static, container, false);
        return view;
    }

    /**
     * 在活动页面创建之后
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "Fragment onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * 页面启动
     */
    @Override
    public void onStart() {
        Log.d(TAG, "Fragment onStart");
        super.onStart();
    }

    /**
     * 页面恢复
     */
    @Override
    public void onResume() {
        Log.d(TAG, "Fragment onResume");
        super.onResume();
    }

    /**
     * 页面暂停
     */
    @Override
    public void onPause() {
        Log.d(TAG, "Fragment onPause");
        super.onPause();
    }

    /**
     * 页面停止
     */
    @Override
    public void onStop() {
        Log.d(TAG, "Fragment onStop");
        super.onStop();
    }

    /**
     * 销毁碎片视图
     */
    @Override
    public void onDestroy() {
        Log.d(TAG, "Fragment onDestroy");
        super.onDestroy();
    }

    /**
     * 把碎片从页面撕下来
     */
    @Override
    public void onDetach() {
        Log.d(TAG, "Fragment onDetach");
        super.onDetach();
    }
}
