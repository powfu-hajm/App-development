package com.example.qxb;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qxb.models.network.ApiResponse;
import com.example.qxb.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private EditText etNickname;
    private Button btnRegister;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        apiService = RetrofitClient.getApiService();
        initViews();
        setupListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etNickname = findViewById(R.id.etNickname);
        btnRegister = findViewById(R.id.btnRegister);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> handleRegister());
    }

    private void handleRegister() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String nickname = etNickname.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 构造注册请求
        RegisterRequest request = new RegisterRequest(username, password, nickname);
        
        btnRegister.setEnabled(false);
        Toast.makeText(this, "注册中...", Toast.LENGTH_SHORT).show();

        apiService.register(request).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                btnRegister.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse.getCode() == 200) {
                        Toast.makeText(RegisterActivity.this, "注册成功，请登录", Toast.LENGTH_SHORT).show();
                        finish(); // 返回登录页
                    } else {
                        Toast.makeText(RegisterActivity.this, "注册失败: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "服务器错误: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                btnRegister.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Register", "Register failed", t);
            }
        });
    }
}
