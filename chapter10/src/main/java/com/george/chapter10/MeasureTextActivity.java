package com.george.chapter10;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.george.chapter10.util.MeasureUtil;

public class MeasureTextActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private TextView tv_desc, tv_text; // 声明一个文本视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_text);
        tv_desc = findViewById(R.id.tv_desc);
        tv_text = findViewById(R.id.tv_text);
        // 初始化文字大小的下拉框
        initSizeSpinner();
    }

    private String[] descArray = {"12sp", "15sp", "17sp", "20sp", "22sp", "25sp", "27sp", "30sp"};
    private int[] sizeArray = {12, 15, 17, 20, 22, 25, 27, 30};

    /**
     * 初始化文字大小选择下拉框
     */
    private void initSizeSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_select, descArray);
        Spinner sp_size = findViewById(R.id.sp_size);
        // 设置下拉框标题：仅在对话框模式才显示标题
        sp_size.setPrompt("请选择文字大小");
        // 设置适配器
        sp_size.setAdapter(adapter);
        // 设置下拉框选项被选中后的监听事件
        sp_size.setOnItemSelectedListener(this);
        // 设置默认选中的选项
        sp_size.setSelection(0);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // 获取文本内容
        String text = tv_text.getText().toString();
        // 选择的文字大小
        int size = sizeArray[position];
        // 计算获取指定文本的宽度（其实就是长度）
        int width = (int) MeasureUtil.getTextWidth(text, size);
        // 计算获取指定文本的高度
        int height = (int) MeasureUtil.getTextHeigth(text, size);
        String desc = String.format("下面文字的宽度是%d，高度是%d", width, height);
        tv_desc.setText(desc);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}