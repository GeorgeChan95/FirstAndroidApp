package com.george.chapter08;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.george.chapter08.util.ToastUtil;

/**
 * 对话框模式的下拉列表
 */
public class SpinnerDialogActivity extends AppCompatActivity {
    // 定义下拉列表需要显示的文本数组
    private final static String[] starArray = {"水星", "金星", "地球", "火星", "木星", "土星"};
    private Spinner sp_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinner_dialog);
        sp_dialog = findViewById(R.id.sp_dialog);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_select, starArray);

        sp_dialog.setAdapter(adapter);
        sp_dialog.setSelection(0);
        sp_dialog.setPrompt("对话框模式显示标题");
        sp_dialog.setOnItemSelectedListener(new MyOnItemSelectedListener());
    }

    private class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ToastUtil.show(SpinnerDialogActivity.this, "选择了: " + starArray[position]);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}