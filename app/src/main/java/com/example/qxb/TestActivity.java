package com.example.qxb;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TestActivity extends AppCompatActivity {

    private String testName;
    private String testDescription;
    private String testDuration;

    private TextView testTitle, testDesc, questionCount;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // 获取传递过来的测试信息
        testName = getIntent().getStringExtra("test_name");
        testDescription = getIntent().getStringExtra("test_description");
        testDuration = getIntent().getStringExtra("test_duration");

        initViews();
        setupTestData();
    }

    private void initViews() {
        testTitle = findViewById(R.id.test_title);
        testDesc = findViewById(R.id.test_description);
        questionCount = findViewById(R.id.question_count);
        btnNext = findViewById(R.id.btn_next);

        // 设置下一步按钮点击事件
        if (btnNext != null) {
            btnNext.setOnClickListener(v -> {
                // 这里可以跳转到下一题或提交测试
                // 暂时先关闭当前页面
                finish();
            });
        }
    }

    private void setupTestData() {
        // 设置测试信息
        if (testTitle != null && testName != null) {
            testTitle.setText(testName);
        }
        if (testDesc != null && testDescription != null) {
            testDesc.setText(testDescription);
        }
        if (questionCount != null && testDuration != null) {
            questionCount.setText(testDuration);
        }

        // 根据不同的测试类型设置不同的内容
        if (testName != null) {
            setupSpecificTestContent(testName);
        }
    }

    private void setupSpecificTestContent(String testName) {
        // 这里可以根据不同的测试类型设置不同的初始问题和选项
        TextView questionText = findViewById(R.id.question_text);

        switch (testName) {
            case "MBTI人格测试":
                if (questionText != null) {
                    questionText.setText("1. 在社交场合中，你通常：");
                }
                setupMBTIOptions();
                break;
            case "抑郁自评量表":
                if (questionText != null) {
                    questionText.setText("1. 我感到情绪沮丧，郁闷");
                }
                setupDepressionOptions();
                break;
            case "焦虑自评量表":
                if (questionText != null) {
                    questionText.setText("1. 我觉得比平时容易紧张和着急");
                }
                setupAnxietyOptions();
                break;
            case "压力评估测试":
                if (questionText != null) {
                    questionText.setText("1. 最近我感到压力很大");
                }
                setupStressOptions();
                break;
        }
    }

    private void setupMBTIOptions() {
        // 设置MBTI测试的选项
        // 这里可以动态设置RadioButton的文本
    }

    private void setupDepressionOptions() {
        // 设置抑郁测试的选项
    }

    private void setupAnxietyOptions() {
        // 设置焦虑测试的选项
    }

    private void setupStressOptions() {
        // 设置压力测试的选项
    }
}