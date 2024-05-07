package com.george.chapter14.listener;

import android.view.View;
import android.widget.AdapterView;

import com.george.chapter14.MediaRecorderActivity;

public class DurationSelectedListener implements AdapterView.OnItemSelectedListener {
    private int[] mDurationArray;
    private MediaRecorderActivity mActivity;

    public DurationSelectedListener(MediaRecorderActivity activity, int[] durationArray) {
        this.mActivity = activity;
        this.mDurationArray = durationArray;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mActivity.setmDuration(mDurationArray[position]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
