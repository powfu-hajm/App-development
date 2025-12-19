package com.example.qxb;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qxb.utils.ThemeManager;

/**
 * Activity基类
 * 负责统一管理主题应用和页面过渡动画
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected ThemeManager themeManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 初始化主题管理器并应用主题（必须在super.onCreate之前）
        themeManager = new ThemeManager(this);
        themeManager.applyTheme(this);

        super.onCreate(savedInstanceState);

        // 设置进入动画
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void finish() {
        super.finish();
        // 设置退出动画
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 设置返回动画
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /**
     * 获取当前主题管理器
     * @return ThemeManager实例
     */
    public ThemeManager getThemeManager() {
        return themeManager;
    }

    /**
     * 切换主题并重启Activity
     * @param themeType 主题类型
     */
    public void switchTheme(int themeType) {
        themeManager.saveTheme(themeType);
        ThemeManager.restartActivity(this);
    }
}
