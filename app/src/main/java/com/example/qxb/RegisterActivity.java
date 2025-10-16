package com.example.qxb;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private ImageButton btnBack;  // 保持为 ImageButton
    private TextInputEditText etUsername, etPhone, etEmail, etPassword, etConfirmPassword;
    private CheckBox cbAgreement;
    private TextView tvUserAgreement, tvPrivacyPolicy, tvLogin;
    private Button btnRegister;  // 这是 MaterialButton
    private ImageButton btnWechat, btnQQ, btnWeibo;  // 这些是 ImageButton

    // 简单的手机号验证正则
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    // 密码强度验证：至少8位，包含字母和数字
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        // 修复：确保使用正确的类型
        btnBack = findViewById(R.id.btnBack);
        etUsername = findViewById(R.id.etUsername);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        cbAgreement = findViewById(R.id.cbAgreement);
        tvUserAgreement = findViewById(R.id.tvUserAgreement);
        tvPrivacyPolicy = findViewById(R.id.tvPrivacyPolicy);
        tvLogin = findViewById(R.id.tvLogin);
        btnRegister = findViewById(R.id.btnRegister);
//        btnWechat = findViewById(R.id.btnWechat);
//        btnQQ = findViewById(R.id.btnQQ);
//        btnWeibo = findViewById(R.id.btnWeibo);

        // 调试：检查视图是否成功初始化
        if (btnBack == null) {
            Toast.makeText(this, "返回按钮未找到", Toast.LENGTH_SHORT).show();
        }
        if (btnRegister == null) {
            Toast.makeText(this, "注册按钮未找到", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners() {
        // 返回按钮
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }

        // 登录链接
        if (tvLogin != null) {
            tvLogin.setOnClickListener(v -> navigateToLogin());
        }

        // 注册按钮
        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> attemptRegister());
        }

        // 第三方登录
        if (btnWechat != null) {
            btnWechat.setOnClickListener(v -> thirdPartyRegister("wechat"));
        }
        if (btnQQ != null) {
            btnQQ.setOnClickListener(v -> thirdPartyRegister("qq"));
        }
        if (btnWeibo != null) {
            btnWeibo.setOnClickListener(v -> thirdPartyRegister("weibo"));
        }
    }

    private void attemptRegister() {
        // 获取输入值
        String username = etUsername.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // 验证输入
        if (!validateInput(username, phone, email, password, confirmPassword)) {
            return;
        }

        // 执行注册逻辑
        performRegistration(username, phone, email, password);
    }

    private boolean validateInput(String username, String phone, String email,
                                  String password, String confirmPassword) {

        // 验证用户名
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("请输入用户名");
            etUsername.requestFocus();
            return false;
        }

        if (username.length() < 2 || username.length() > 20) {
            etUsername.setError("用户名长度应为2-20个字符");
            etUsername.requestFocus();
            return false;
        }

        // 验证手机号
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("请输入手机号");
            etPhone.requestFocus();
            return false;
        }

        if (!PHONE_PATTERN.matcher(phone).matches()) {
            etPhone.setError("请输入有效的手机号码");
            etPhone.requestFocus();
            return false;
        }

        // 验证邮箱（可选）
        if (!TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("请输入有效的邮箱地址");
            etEmail.requestFocus();
            return false;
        }

        // 验证密码
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("请输入密码");
            etPassword.requestFocus();
            return false;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            etPassword.setError("密码至少8位，且包含字母和数字");
            etPassword.requestFocus();
            return false;
        }

        // 验证确认密码
        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("请确认密码");
            etConfirmPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("两次输入的密码不一致");
            etConfirmPassword.requestFocus();
            return false;
        }

        // 验证用户协议
        if (!cbAgreement.isChecked()) {
            Toast.makeText(this, "请阅读并同意用户协议和隐私政策", Toast.LENGTH_SHORT).show();
            cbAgreement.requestFocus();
            return false;
        }

        return true;
    }

    private void performRegistration(String username, String phone, String email, String password) {
        // 显示加载状态
        btnRegister.setEnabled(false);
        btnRegister.setText("注册中...");

        // 模拟网络请求延迟
        new android.os.Handler().postDelayed(() -> {
            // 这里应该是实际的注册API调用
            boolean registrationSuccess = simulateRegistration(username, phone, email, password);

            runOnUiThread(() -> {
                if (registrationSuccess) {
                    // 注册成功
                    handleRegistrationSuccess(username);
                } else {
                    // 注册失败
                    handleRegistrationFailure();
                }
            });
        }, 2000);
    }

    private boolean simulateRegistration(String username, String phone, String email, String password) {
        // 模拟注册逻辑
        // 在实际应用中，这里应该调用后端API
        try {
            // 模拟网络请求
            Thread.sleep(1000);

            // 简单的模拟：用户名不能是"admin"，手机号不能是"13800138000"
            return !username.equals("admin") && !phone.equals("13800138000");
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void handleRegistrationSuccess(String username) {
        Toast.makeText(this, "注册成功！欢迎 " + username, Toast.LENGTH_SHORT).show();

        // 跳转到主界面
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }

    private void handleRegistrationFailure() {
        btnRegister.setEnabled(true);
        btnRegister.setText("注册");

        Toast.makeText(this, "注册失败，请稍后重试", Toast.LENGTH_SHORT).show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void thirdPartyRegister(String platform) {
        Toast.makeText(this, "正在通过" + getPlatformName(platform) + "注册", Toast.LENGTH_SHORT).show();

        // 这里应该集成第三方登录SDK
        // 例如：微信登录、QQ登录、微博登录等

        // 模拟第三方登录成功
        new android.os.Handler().postDelayed(() -> {
            runOnUiThread(() -> {
                Toast.makeText(this, getPlatformName(platform) + "登录成功", Toast.LENGTH_SHORT).show();

                // 跳转到主界面
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            });
        }, 1500);
    }

    private String getPlatformName(String platform) {
        switch (platform) {
            case "wechat":
                return "微信";
            case "qq":
                return "QQ";
            case "weibo":
                return "微博";
            default:
                return "第三方平台";
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}