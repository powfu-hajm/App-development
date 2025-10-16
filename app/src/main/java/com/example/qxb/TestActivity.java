package com.example.qxb;

import android.os.Bundle;
import android.view.View; // 添加这行import
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class TestActivity extends AppCompatActivity {

    private TestViewModel testViewModel;
    private TextView tvQuestion;
    private RadioGroup rgOptions;
    private Button btnNext;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 先注释掉setContentView，等XML修复后再取消注释
        // setContentView(R.layout.activity_test_fragment);

        // 使用临时UI让项目先编译通过
        initializeSimpleUI();

        // 初始化ViewModel
        testViewModel = new ViewModelProvider(this).get(TestViewModel.class);
    }

    /**
     * 临时初始化简单UI
     */
    private void initializeSimpleUI() {
        // 创建简单的布局
        androidx.appcompat.widget.LinearLayoutCompat layout = new androidx.appcompat.widget.LinearLayoutCompat(this);
        layout.setOrientation(androidx.appcompat.widget.LinearLayoutCompat.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        // 问题显示
        tvQuestion = new TextView(this);
        tvQuestion.setText("心理测试问题将显示在这里");
        tvQuestion.setTextSize(18);
        layout.addView(tvQuestion);

        // 选项组
        rgOptions = new RadioGroup(this);
        rgOptions.setOrientation(RadioGroup.VERTICAL);

        // 添加示例选项
        for (int i = 1; i <= 4; i++) {
            android.widget.RadioButton radioButton = new android.widget.RadioButton(this);
            radioButton.setText("选项 " + i);
            radioButton.setId(i);
            rgOptions.addView(radioButton);
        }
        layout.addView(rgOptions);

        // 下一步按钮
        btnNext = new Button(this);
        btnNext.setText("下一题");
        btnNext.setPadding(20, 20, 20, 20);
        layout.addView(btnNext);

        // 提交按钮
        btnSubmit = new Button(this);
        btnSubmit.setText("提交测试");
        btnSubmit.setPadding(20, 20, 20, 20);
        layout.addView(btnSubmit);

        setContentView(layout);

        // 设置点击事件
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = rgOptions.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    testViewModel.answerQuestion(selectedId);
                    testViewModel.nextQuestion();
                    showToast("已选择选项 " + selectedId);
                } else {
                    showToast("请选择一个选项");
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testViewModel.submitTest();
                showToast("测试提交成功");
                finish();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}