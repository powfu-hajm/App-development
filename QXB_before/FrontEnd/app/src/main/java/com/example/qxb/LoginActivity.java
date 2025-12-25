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

        // 初始化RetrofitClient上下文
        RetrofitClient.init(this);

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

        // 检查是否有账户注销的提示
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("account_deleted", false)) {
            Toast.makeText(this, "您的账户已成功注销", Toast.LENGTH_LONG).show();
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

        // 检查网络连接
        if (!RetrofitClient.isNetworkAvailable(this)) {
            Toast.makeText(this, "网络连接不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
            return;
        }

        // 禁用按钮防止重复点击
        btnLogin.setEnabled(false);
        Toast.makeText(LoginActivity.this, "登录中...", Toast.LENGTH_SHORT).show();

        LoginDTO dto = new LoginDTO(username, password);

        apiService.login(dto).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call,
                                   Response<ApiResponse<String>> response) {

                btnLogin.setEnabled(true);

                if (!response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this,
                            "服务器异常: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    Log.e("Login", "HTTP错误码: " + response.code());
                    return;
                }

                ApiResponse<String> body = response.body();
                if (body == null) {
                    Toast.makeText(LoginActivity.this,
                            "服务器响应为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // 使用isSuccess()方法而不是直接比较code
                if (!body.isSuccess()) {
                    Toast.makeText(LoginActivity.this,
                            "登录失败: " + body.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // 登录成功，直接获取Token字符串
                String token = body.getData();
                if (token == null || token.isEmpty()) {
                    Toast.makeText(LoginActivity.this,
                            "登录数据异常",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // 保存登录信息 - 使用新的方法名
                sessionManager.saveLoginInfo(username, token);

                // 注意：这里我们还没有用户ID，需要后续获取
                // 可以在MainActivity中获取用户信息后保存用户ID

                // 设置全局Token
                RetrofitClient.authToken = token;

                Log.d("Login", "登录成功，用户名: " + username);
                Log.d("Login", "Token: " + token.substring(0, Math.min(token.length(), 50)) + "...");

                // 跳转到主页
                Toast.makeText(LoginActivity.this,
                        "登录成功",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                btnLogin.setEnabled(true);

                String errorMessage = "登录失败";
                if (t.getMessage() != null) {
                    errorMessage += ": " + t.getMessage();
                    if (t.getMessage().contains("timeout")) {
                        errorMessage = "连接超时，请检查服务器地址和网络连接";
                    } else if (t.getMessage().contains("Unable to resolve host")) {
                        errorMessage = "无法连接到服务器，请检查网络设置和服务器地址";
                    }
                }

                Toast.makeText(LoginActivity.this,
                        errorMessage,
                        Toast.LENGTH_SHORT).show();
                Log.e("Login", "登录请求失败", t);
            }
        });
    }
}