package com.george.chapter08;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.george.chapter08.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class SpinnerDropdownActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    // 定义下拉列表需要显示的文本数组
    private final static String[] starArray = {"水星", "金星", "地球", "火星", "木星", "土星"};
    private Spinner sp_dropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinner_dropdown);
        sp_dropdown = findViewById(R.id.sp_dropdown);

        // 数组适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_select, starArray);

        // 设置下拉框标题：仅在对话框模式才显示标题
        sp_dropdown.setPrompt("选择行星");
        // 设置适配器
        sp_dropdown.setAdapter(adapter);
        // 设置下拉框选项被选中后的监听事件
        sp_dropdown.setOnItemSelectedListener(this);
        // 设置默认选中的选项
        sp_dropdown.setSelection(0);
    }

    /**
     * 选中某个选项的回调方法
     *
     * @param parent 当前所操作的Spinner，当某一个Activity中有多个Spinner时，
     *               可以根据parent.getId()与R.id.currentSpinner是否相等，来判断是否你当前操作的Spinner，
     *               一般在onItemSelected方法中，通过switch...case...语句来解决多个Spinner的情况
     * @param view 经过我的测试，在同一个Activity的多个Spinner中，无论你操作任何一个Spinner，
     *             选中任何一个下拉值，view(或者说view.getId())的值都不变，因此，我们可以不用关心这个view，因为一般不可能用得到。
     * @param position 是你选中的某个Spinner中的某个下拉值所在的位置，一般自上而下从0开始。
     * @param id 是你选中的某个Spinner中的某个下来值所在的行，一般自上而下从0开始，id的值与第三个参数position的值一直是一样的
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String name = starArray[position];
        ToastUtil.show(this, "选择了：" + name);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}