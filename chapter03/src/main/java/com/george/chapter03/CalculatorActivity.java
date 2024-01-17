package com.george.chapter03;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CalculatorActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_result;
    // 第一个操作数
    private String firstNum = "";
    // 运算符
    private String operator = "";
    // 第二个操作数
    private String secondNum = "";
    // 当前的计算结果
    private String result = "";
    // 显示的文本内容
    private String showText = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        // 从布局文件中获取名叫tv_result的文本视图
        tv_result = findViewById(R.id.tv_result);
        // 下面给每个按钮控件都注册了点击监听器
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_divide).setOnClickListener(this); // “除法”按钮
        findViewById(R.id.btn_multiply).setOnClickListener(this); // “乘法”按钮
        findViewById(R.id.btn_clear).setOnClickListener(this); // “清除”按钮
        findViewById(R.id.btn_seven).setOnClickListener(this); // 数字7
        findViewById(R.id.btn_eight).setOnClickListener(this); // 数字8
        findViewById(R.id.btn_nine).setOnClickListener(this); // 数字9
        findViewById(R.id.btn_plus).setOnClickListener(this); // “加法”按钮
        findViewById(R.id.btn_four).setOnClickListener(this); // 数字4
        findViewById(R.id.btn_five).setOnClickListener(this); // 数字5
        findViewById(R.id.btn_six).setOnClickListener(this); // 数字6
        findViewById(R.id.btn_minus).setOnClickListener(this); // “减法”按钮
        findViewById(R.id.btn_one).setOnClickListener(this); // 数字1
        findViewById(R.id.btn_two).setOnClickListener(this); // 数字2
        findViewById(R.id.btn_three).setOnClickListener(this); // 数字3
        findViewById(R.id.ib_sqrt).setOnClickListener(this); // “开平方”按钮
        findViewById(R.id.btn_reciprocal).setOnClickListener(this); // 求倒数按钮
        findViewById(R.id.btn_zero).setOnClickListener(this); // 数字0
        findViewById(R.id.btn_dot).setOnClickListener(this); // “小数点”按钮
        findViewById(R.id.btn_equal).setOnClickListener(this); // “等号”按钮
    }

    @Override
    public void onClick(View v) {
        String inputText = "";
        // 如果是开根号按钮
        if (v.getId() == R.id.ib_sqrt) {
            inputText = "√";
        } else {
            // 除了开根号之外的其他按钮
            inputText = ((TextView)v).getText().toString();
        }

        switch (v.getId()) {
            case R.id.btn_clear: // 清除按钮
                Log.d("Calculator", "点击了【清除】按钮");
                clear();
                break;
            case R.id.btn_cancel: // 取消按钮
                clear();
                break;
            // 点击了加、减、乘、除按钮
            case R.id.btn_plus:
            case R.id.btn_minus:
            case R.id.btn_multiply:
            case R.id.btn_divide:
                operator = inputText; // 运算符赋值
                refreshText(showText + operator);
                break;
            case R.id.btn_equal: // 等号按钮
                // 获取四则运算结果
                Double calculateResult = calculateFour();
                if (calculateResult != null) {
                    refreshOperate(calculateResult.toString());
                    refreshText(showText + "=" +  result);
                }
                break;
            case R.id.ib_sqrt: // 开根号按钮
                double sqrt_result = Math.sqrt(Double.parseDouble(firstNum));
                refreshOperate(String.valueOf(sqrt_result));
                refreshText(showText + "√=" + result);
                break;
            case R.id.btn_reciprocal: // 求倒数按钮
                double reciprocal_result = 1.0 / Double.parseDouble(firstNum);
                refreshOperate(String.valueOf(reciprocal_result));
                refreshText(showText + "/=" + result);
                break;
            default: // 点击了其他按钮，包括数字和小数点
                // 已有运算结果的情况下
                if (result.length() > 0 && operator.equals("")) {
                    clear();
                }

                // 无运算符，拼接第一个操作数
                if (operator.equals("")) {
                    if (firstNum.equals("") && inputText.equals(".")) { // . 不能作为运算数字的首字符
                        break;
                    }
                    firstNum = firstNum + inputText;
                } else { // 有运算符，拼接第二个操作数
                    if (secondNum.equals("") && inputText.equals(".")) {
                        break;
                    }
                    secondNum = secondNum + inputText;
                }

                // 将输入的内容展示到页面上
                if (showText.equals("0") && !inputText.equals(".")) { // 整数不能是0开头
                    refreshText(inputText);
                } else {
                    refreshText(showText + inputText);
                }
                break;
        }

    }

    /**
     * 进行四则运算，并返回运算结果
     * @return 计算结果
     */
    private Double calculateFour() {
        switch (operator) {
            case "＋":
                return Double.valueOf(firstNum) + Double.valueOf(secondNum);
            case "－":
                return Double.valueOf(firstNum) - Double.valueOf(secondNum);
            case "×":
                return Double.valueOf(firstNum) * Double.valueOf(secondNum);
            default:
                if (secondNum.equals("0")) {
                    Toast.makeText(CalculatorActivity.this, "除数不能为0", Toast.LENGTH_SHORT).show();
                    return null;
                }
                return Double.valueOf(firstNum) / Double.valueOf(secondNum);
        }
    }

    /**
     * 刷新运算结果和文本显示
     */
    private void clear() {
        // 刷新运算结果
        refreshOperate("");
        // 刷新文本框
        refreshText("");
    }


    /**
     * 刷新计算器的运算结果,
     * @param newResult 需要刷新显示到文本框的内容
     */
    private void refreshOperate(String newResult) {
        result = newResult;
        firstNum = result;
        secondNum = "";
        operator = "";
    }

    /**
     * 刷新文本框的内容
     * @param text 要展示在文本框的内容
     */
    private void refreshText(String text) {
        showText = text;
        tv_result.setText(showText);
    }
}