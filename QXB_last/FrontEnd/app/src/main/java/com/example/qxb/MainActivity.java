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
import com.example.qxb.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private SessionManager sessionManager;

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
            RetrofitClient.authToken = sessionManager.getToken();
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
    
    // 退出登录方法
    public void logout() {
        sessionManager.logout();
        RetrofitClient.authToken = null;
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
