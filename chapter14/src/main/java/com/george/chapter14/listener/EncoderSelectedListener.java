package com.george.chapter14.listener;

import android.view.View;
import android.widget.AdapterView;

import com.george.chapter14.MediaRecorderActivity;

/**
 * 自定义音频编码选择下拉监听器
 */
public class EncoderSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {

    private int[] mEncoderArray;
    private MediaRecorderActivity mActivity;

    public EncoderSelectedListener(MediaRecorderActivity activity, int[] encoderArray) {
        this.mActivity = activity;
        this.mEncoderArray = encoderArray;
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
        // 给活动页面的音频编码格式字段赋值
        mActivity.setmAudioEncoder(mEncoderArray[position]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
