package com.george.chapter10;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.george.chapter10.widget.OvalView;

public class ViewInvalidateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "GeorgeTag";

    private OvalView ov_validate;

    private String[] refreshArray = {
            "主线程调用invalidate",
            "主线程调用postInvalidate",
            "延迟3秒后刷新",
            "分线程调用invalidate",
            "分线程调用postInvalidate"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_invalidate);

        ov_validate = findViewById(R.id.ov_validate);
        // 初始化刷新方式的下拉框
        initRefreshSpinner();

    }

    /**
     * 初始化下拉框
     */
    private void initRefreshSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_select, refreshArray);
        Spinner spinner = findViewById(R.id.sp_refresh);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(0);
        spinner.setPrompt("请选择一个刷新方式");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: // 主线程调用invalidate
                // 刷新视图（用于主线程）
                ov_validate.invalidate();
                break;
            case 1: // 主线程调用postInvalidate
                // 刷新视图（主线程和分线程均可使用）
                ov_validate.postInvalidate();
                break;
            case 2: // 延迟3秒后刷新
                // 延迟若干时间后再刷新视图
                ov_validate.postInvalidateDelayed(3000);
                break;
            case 3: // 分线程调用invalidate
                // invalidate不是线程安全的，虽然下面代码在分线程中调用invalidate方法也没报错，但在复杂场合可能出错
                new Thread(() -> ov_validate.invalidate()).start();
                break;
            case 4: // 分线程调用postInvalidate
                // postInvalidate是线程安全的，分线程中建议调用postInvalidate方法来刷新视图
                new Thread(() -> ov_validate.postInvalidate()).start();
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}