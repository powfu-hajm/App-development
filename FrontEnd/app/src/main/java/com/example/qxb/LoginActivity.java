package com.example.qxb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qxb.models.network.ApiResponse;
import com.example.qxb.models.dto.LoginDTO;
import com.example.qxb.models.dto.LoginResult;
import com.example.qxb.RetrofitClient;
import com.example.qxb.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnRegister;
    private EditText etUsername;
    private EditText etPassword;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化
        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getApiService();

        // 检查是否已登录，如果已登录且Token有效，直接进主页
        if (sessionManager.isLoggedIn()) {
            String token = sessionManager.getToken();
            String username = sessionManager.getUsername();
            // 设置全局 Token
            RetrofitClient.authToken = token;
            Log.d("Login", "自动登录: " + username);
            
            // 跳转到主页
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());
        
        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void handleLogin() {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        // 禁用按钮防止重复点击
        btnLogin.setEnabled(false);
        Toast.makeText(LoginActivity.this, "登录中...", Toast.LENGTH_SHORT).show();

        LoginDTO dto = new LoginDTO(username, password);

        apiService.login(dto).enqueue(new Callback<ApiResponse<LoginResult>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResult>> call,
                                   Response<ApiResponse<LoginResult>> response) {

                btnLogin.setEnabled(true);

                if (!response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this,
                            "服务器异常: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                ApiResponse<LoginResult> body = response.body();
                if (body == null) {
                    Toast.makeText(LoginActivity.this,
                            "响应为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!body.isSuccess()) {
                    Toast.makeText(LoginActivity.this,
                            body.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // 取 Token
                String token = body.getData().getToken();
                Toast.makeText(LoginActivity.this,
                        "登录成功 Token：" + token,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResult>> call, Throwable t) {
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this,
                        "请求失败：" + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
}
