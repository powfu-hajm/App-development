package com.example.qxb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.qxb.R;

public class TestActivity extends AppCompatActivity {

    private TextView tvTestTitle, tvTestDescription;
    private Button btnStartTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        initViews();
        setupToolbar();
        setupClickListeners();
        loadTestData();
    }

    private void initViews() {
        // 初始化工具栏
        Toolbar toolbar = findViewById(R.id.toolbar);

        // 初始化其他视图
        tvTestTitle = findViewById(R.id.tvTestTitle);
        tvTestDescription = findViewById(R.id.tvTestDescription);
        btnStartTest = findViewById(R.id.btnStartTest);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 设置返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 设置返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadTestData() {
        // 从Intent获取数据
        Intent intent = getIntent();
        if (intent != null) {
            String testTitle = intent.getStringExtra("test_title");
            String testDescription = intent.getStringExtra("test_description");

            if (testTitle != null) {
                tvTestTitle.setText(testTitle);
            }

            if (testDescription != null) {
                tvTestDescription.setText(testDescription);
            }
        }
    }

    private void setupClickListeners() {
        btnStartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTest();
            }
        });
    }

    private void startTest() {
        // 这里可以跳转到具体的测试页面
        // 暂时显示提示信息
        android.widget.Toast.makeText(this, "心理测试功能开发中...", android.widget.Toast.LENGTH_LONG).show();

        // 示例：可以跳转到具体的测试问题页面
        // Intent intent = new Intent(this, TestQuestionsActivity.class);
        // startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}