package com.example.qxb;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置布局文件 - 需要创建对应的 XML 布局
        // setContentView(R.layout.activity_login);

        // 临时显示简单的登录界面
        showTemporaryLoginView();

        // 显示提示信息
        Toast.makeText(this, "正在加载登录页面...", Toast.LENGTH_SHORT).show();
    }

    private void showTemporaryLoginView() {
        // 创建垂直布局
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setGravity(android.view.Gravity.CENTER);
        layout.setPadding(100, 100, 100, 100);

        // 创建标题
        android.widget.TextView title = new android.widget.TextView(this);
        title.setText("青心伴 - 登录");
        title.setTextSize(24);
        title.setTextColor(getResources().getColor(android.R.color.black));
        title.setGravity(android.view.Gravity.CENTER);
        title.setPadding(0, 0, 0, 50);

        // 创建登录按钮
        btnLogin = new Button(this);
        btnLogin.setText("登录");
        btnLogin.setTextSize(16);
        btnLogin.setPadding(50, 20, 50, 20);
        android.widget.LinearLayout.LayoutParams loginParams =
                new android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                );
        loginParams.setMargins(0, 0, 0, 20);
        btnLogin.setLayoutParams(loginParams);

        // 创建注册按钮
        btnRegister = new Button(this);
        btnRegister.setText("注册");
        btnRegister.setTextSize(16);
        btnRegister.setPadding(50, 20, 50, 20);
        android.widget.LinearLayout.LayoutParams registerParams =
                new android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                );
        btnRegister.setLayoutParams(registerParams);

        // 添加视图到布局
        layout.addView(title);
        layout.addView(btnLogin);
        layout.addView(btnRegister);

        // 设置内容视图
        setContentView(layout);

        // 设置按钮点击监听器
        setupButtonListeners();
    }

    private void setupButtonListeners() {
        if (btnLogin != null) {
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validateLogin()) {
                        try {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // 结束当前Activity
                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this, "主页尚未就绪", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "请输入有效的登录信息", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (btnRegister != null) {
            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                        startActivity(intent);
                        // 不finish()，以便用户注册后可以返回登录页面
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "注册页面尚未就绪", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean validateLogin() {
        // 示例验证逻辑
        // 实际项目中应该检查用户名密码等
        return true; // 暂时直接返回true用于测试
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

    // 如果需要处理返回键
    /*
    @Override
    public void onBackPressed() {
        // 如果需要在登录页面按返回键有特殊处理，可以在这里实现
        // 例如：双击退出应用
        super.onBackPressed();
    }
    */
}