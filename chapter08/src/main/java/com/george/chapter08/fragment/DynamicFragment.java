package com.george.chapter08.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.george.chapter08.R;

public class DynamicFragment extends Fragment {
    private static final String TAG = "GeorgeTag";

    /**
     * 生成动态Fragment实例，并添加相应的参数
     * @param position
     * @param pic
     * @param description
     * @return
     */
    public static Fragment newInstance(int position, int pic, String description) {
        DynamicFragment fragment = new DynamicFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putInt("pic", pic);
        bundle.putString("desc", description);
        fragment.setArguments(bundle);
        return fragment;
    }

    // 从包裹取出位置序号
    private int getPosition(){
        return getArguments().getInt("position", 0);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "DynamicFragment onAttach, position=" + getPosition());
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "DynamicFragment onCreate, position=" + getPosition());
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "DynamicFragment onCreateView, position=" + getPosition());
        View view = inflater.inflate(R.layout.fragment_dynamic, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            int pic = arguments.getInt("pic");
            String desc = arguments.getString("desc");
            ImageView iv_pic = view.findViewById(R.id.iv_pic);
            TextView tv_desc = view.findViewById(R.id.tv_desc);
            iv_pic.setImageResource(pic);
            tv_desc.setText(desc);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "DynamicFragment onViewCreated, position=" + getPosition());
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "DynamicFragment onResume, position=" + getPosition());
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "DynamicFragment onPause, position=" + getPosition());
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "DynamicFragment onStop, position=" + getPosition());
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "DynamicFragment onDestroyView, position=" + getPosition());
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "DynamicFragment onDestroy, position=" + getPosition());
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "DynamicFragment onDetach, position=" + getPosition());
        super.onDetach();
    }
}
