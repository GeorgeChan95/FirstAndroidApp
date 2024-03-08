package com.george.chapter08;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class FragmentStaticActivity extends AppCompatActivity {
    private static final String TAG = "GeorgeTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Activity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_static);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "Activity onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Activity onStart");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Activity onStart");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "Activity onStart");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "Activity onStart");
        super.onResume();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "Activity onStart");
        super.onRestart();
    }
}