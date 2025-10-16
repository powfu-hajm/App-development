package com.example.qxb;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnRegister;
    private EditText etUsername;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置布局文件
        setContentView(R.layout.activity_login);

        // 初始化视图
        initViews();

        // 设置按钮监听器
        setupButtonListeners();

        Toast.makeText(this, "欢迎使用青心伴", Toast.LENGTH_SHORT).show();
    }

    private void initViews() {
        // 初始化按钮和输入框
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        // 检查视图是否成功初始化
        if (btnLogin == null) {
            Toast.makeText(this, "登录按钮未找到", Toast.LENGTH_SHORT).show();
        }
        if (btnRegister == null) {
            Toast.makeText(this, "注册按钮未找到", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupButtonListeners() {
        // 登录按钮点击事件
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
                            Toast.makeText(LoginActivity.this, "主页尚未就绪: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "登录按钮未初始化", Toast.LENGTH_SHORT).show();
        }

        // 注册按钮点击事件
        if (btnRegister != null) {
            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                        startActivity(intent);
                        // 不finish()，以便用户注册后可以返回登录页面
                        Toast.makeText(LoginActivity.this, "跳转到注册页面", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "注册页面尚未就绪: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(this, "注册按钮未初始化", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateLogin() {
        // 简单的验证逻辑
        if (etUsername != null && etPassword != null) {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            return !username.isEmpty() && !password.isEmpty();
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Activity恢复时刷新界面
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Activity暂停时的逻辑
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清理资源
    }

    @Override
    public void onBackPressed() {
        // 退出应用确认
        // super.onBackPressed();
        // 暂时直接退出
        finish();
    }
}