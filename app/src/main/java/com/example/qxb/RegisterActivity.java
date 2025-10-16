package com.example.qxb;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 显示提示信息，帮助定位问题
        Toast.makeText(this, "正在检查布局文件问题...", Toast.LENGTH_LONG).show();

        // 这里可以添加一些基本的UI，避免完全空白
        setBasicContentView();
    }

    private void setBasicContentView() {
        // 创建垂直布局容器
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setGravity(android.view.Gravity.CENTER);
        layout.setPadding(100, 100, 100, 100);

        // 创建标题文本
        android.widget.TextView title = new android.widget.TextView(this);
        title.setText("注册页面");
        title.setTextSize(24);
        title.setTextColor(getResources().getColor(android.R.color.black));
        title.setGravity(android.view.Gravity.CENTER);
        title.setPadding(0, 0, 0, 50);

        // 创建测试按钮
        Button button = new Button(this);
        button.setText("测试按钮 - 布局文件有问题");

        android.widget.LinearLayout.LayoutParams buttonParams =
                new android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                );
        button.setLayoutParams(buttonParams);
        button.setPadding(50, 20, 50, 20);

        // 创建返回按钮
        Button backButton = new Button(this);
        backButton.setText("返回登录");

        android.widget.LinearLayout.LayoutParams backButtonParams =
                new android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                );
        backButtonParams.setMargins(0, 20, 0, 0);
        backButton.setLayoutParams(backButtonParams);
        backButton.setPadding(50, 20, 50, 20);

        // 添加视图到布局
        layout.addView(title);
        layout.addView(button);
        layout.addView(backButton);

        // 设置内容视图
        setContentView(layout);

        // 设置按钮点击监听器
        setupButtonListeners(button, backButton);
    }

    private void setupButtonListeners(Button testButton, Button backButton) {
        // 测试按钮点击事件
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegisterActivity.this, "请检查activity_register.xml文件", Toast.LENGTH_SHORT).show();
            }
        });

        // 返回按钮点击事件
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回到登录页面
                try {
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // 结束当前注册页面
                } catch (Exception e) {
                    Toast.makeText(RegisterActivity.this, "无法返回登录页面", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Activity 恢复时的逻辑
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Activity 暂停时的逻辑
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清理资源
    }

    // 处理返回键
    @Override
    public void onBackPressed() {
        // 返回登录页面
        try {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            super.onBackPressed();
        }
    }
}