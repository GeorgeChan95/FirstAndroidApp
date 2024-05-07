package com.george.chapter14.listener;

import android.view.View;
import android.widget.AdapterView;

import com.george.chapter14.MediaRecorderActivity;

public class FormatSelectedListener implements AdapterView.OnItemSelectedListener {
    private int[] mFormatArray;
    private MediaRecorderActivity mActivity;

    public FormatSelectedListener(MediaRecorderActivity activity, int[] formatArray) {
        this.mActivity = activity;
        this.mFormatArray = formatArray;
    }

    /**
     * 选中下拉选项，触发 onItemSelected方法
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // 给活动页面的音频输出格式字段赋值
        mActivity.setmOutputFormat(mFormatArray[position]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
