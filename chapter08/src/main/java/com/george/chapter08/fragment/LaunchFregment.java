package com.george.chapter08.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.george.chapter08.R;
import com.george.chapter08.util.ToastUtil;

public class LaunchFregment extends Fragment {
    private static final String TAG = "GerogeTag";

    public static LaunchFregment newInstance(int position, int count, int imageId) {
        LaunchFregment fragment = new LaunchFregment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putInt("count", count);
        bundle.putInt("imageId", imageId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "LaunchFregment onCreateView");
        Context mContext = getContext();
        View view = inflater.inflate(R.layout.item_lanuch, container, false);
        Bundle arguments = getArguments();

        if (arguments != null) {
            int position = arguments.getInt("position");
            int imageId = arguments.getInt("imageId");
            int count = arguments.getInt("count");

            ImageView iv_lanuch = view.findViewById(R.id.iv_lanuch);
            RadioGroup rg_indicate = view.findViewById(R.id.rg_indicate);
            Button btn_start = view.findViewById(R.id.btn_start);
            iv_lanuch.setImageResource(imageId);

            // 每个页面都分配一组对应的单选按钮
            for (int j = 0; j < count; j++) {
                RadioButton radio = new RadioButton(mContext);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                radio.setLayoutParams(params);
                radio.setPadding(10, 10, 10, 10);
                rg_indicate.addView(radio);
            }
            // 与页面下标索引相同的Radio，设置选中状态
            ((RadioButton) rg_indicate.getChildAt(position)).setChecked(true);

            // 如果是最后一个页面，显示按钮
            if (position == count - 1) {
                btn_start.setVisibility(View.VISIBLE);
                btn_start.setOnClickListener(v -> ToastUtil.show(mContext, "即将打开应用"));
            }
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
