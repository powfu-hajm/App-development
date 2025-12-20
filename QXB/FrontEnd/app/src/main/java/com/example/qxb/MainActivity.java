package com.example.qxb;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.qxb.DiaryFragment;
import com.example.qxb.HomeFragment;
import com.example.qxb.ChatFragment;
import com.example.qxb.TestFragment;
import com.example.qxb.ProfileFragment;
import com.example.qxb.models.User;
import com.example.qxb.models.network.ApiResponse;
import com.example.qxb.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private SessionManager sessionManager;

    // 存储当前用户信息
    private static User currentUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // === 登录检查 ===
        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            // 未登录，跳转到登录页
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        } else {
            // 已登录，确保 RetrofitClient 有 Token
            String token = sessionManager.getToken();
            if (token != null && !token.isEmpty()) {
                RetrofitClient.authToken = token;
                Log.d("MainActivity", "从SessionManager恢复Token");
            }

            // 从SessionManager获取用户名，初始化一个基础用户对象
            String username = sessionManager.getUsername();
            if (username != null && currentUser == null) {
                currentUser = new User();
                currentUser.setUsername(username);
                Log.d("MainActivity", "初始化基础用户对象: " + username);
            }
        }

        setContentView(R.layout.activity_main);

        initViews();
        setupBottomNavigation();
        // 启用矢量图支持
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        // 默认显示首页
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // 加载完整的用户信息
        loadUserInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 检查网络连接
        if (!RetrofitClient.isNetworkAvailable(this)) {
            Toast.makeText(this, "网络连接不可用，部分功能可能受限", Toast.LENGTH_LONG).show();
        }
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_chat) {
                selectedFragment = new ChatFragment();
            } else if (itemId == R.id.nav_diary) {
                selectedFragment = new DiaryFragment();
            } else if (itemId == R.id.nav_test) {
                selectedFragment = new TestFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            Log.d("FRAGMENT", "成功加载Fragment: " + fragment.getClass().getSimpleName());
        } catch (Exception e) {
            Log.e("FRAGMENT_ERROR", "加载Fragment失败: " + e.getMessage(), e);
            Toast.makeText(this, "加载Fragment失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // 添加切换方法供其他Fragment调用
    public void switchToChat() {
        bottomNavigationView.setSelectedItemId(R.id.nav_chat);
    }

    public void switchToDiary() {
        bottomNavigationView.setSelectedItemId(R.id.nav_diary);
    }

    public void switchToTest() {
        bottomNavigationView.setSelectedItemId(R.id.nav_test);
    }

    // 更新当前用户信息
    public void updateCurrentUser(User user) {
        if (user != null) {
            currentUser = user;
            Log.d("MainActivity", "用户信息更新成功");
            Log.d("MainActivity", "用户ID: " + user.getId());
            Log.d("MainActivity", "用户名: " + user.getUsername());
            Log.d("MainActivity", "昵称: " + user.getNickname());
            Log.d("MainActivity", "创建时间: " + user.getCreateTime());
        } else {
            Log.w("MainActivity", "更新用户信息: user为null");
        }
    }

    // 获取当前用户信息
    public User getCurrentUser() {
        return currentUser;
    }

    // 加载用户信息（可从外部调用）
    public void loadUserInfo() {
        ApiService apiService = RetrofitClient.getApiService();
        if (apiService == null) {
            Log.e("MainActivity", "ApiService为空");
            return;
        }

        Log.d("MainActivity", "开始加载用户信息...");
        apiService.getUserInfo().enqueue(new retrofit2.Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(retrofit2.Call<ApiResponse<User>> call, retrofit2.Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        User user = apiResponse.getData();
                        updateCurrentUser(user);
                        Log.d("MainActivity", "用户信息加载成功: " + user.getDisplayName());
                    } else {
                        Log.w("MainActivity", "API响应失败: " + apiResponse.getMessage());
                    }
                } else {
                    Log.e("MainActivity", "HTTP响应失败: " + response.code());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ApiResponse<User>> call, Throwable t) {
                Log.e("MainActivity", "加载用户信息失败: " + t.getMessage());
            }
        });
    }

    // 退出登录方法
    public void logout() {
        sessionManager.logout();
        RetrofitClient.authToken = null;
        currentUser = null;
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
