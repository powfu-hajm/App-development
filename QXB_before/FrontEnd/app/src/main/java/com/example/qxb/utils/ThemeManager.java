package com.example.qxb.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * 主题管理器
 * 负责应用主题的切换和持久化存储
 */
public class ThemeManager {

    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_THEME_MODE = "theme_mode";

    // 主题模式常量
    public static final int THEME_LIGHT = 0;  // 浅色主题
    public static final int THEME_DARK = 1;   // 深色主题
    public static final int THEME_SYSTEM = 2; // 跟随系统

    private final SharedPreferences prefs;
    private static ThemeManager instance;

    private ThemeManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 获取 ThemeManager 实例
     */
    public static synchronized ThemeManager getInstance(Context context) {
        if (instance == null) {
            instance = new ThemeManager(context);
        }
        return instance;
    }

    /**
     * 获取当前主题模式
     */
    public int getThemeMode() {
        return prefs.getInt(KEY_THEME_MODE, THEME_LIGHT);
    }

    /**
     * 设置主题模式并应用
     */
    public void setThemeMode(int themeMode) {
        prefs.edit().putInt(KEY_THEME_MODE, themeMode).apply();
        applyTheme(themeMode);
    }

    /**
     * 应用主题
     */
    public void applyTheme(int themeMode) {
        switch (themeMode) {
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case THEME_SYSTEM:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    /**
     * 应用保存的主题
     */
    public void applySavedTheme() {
        applyTheme(getThemeMode());
    }

    /**
     * 切换主题 (浅色 <-> 深色)
     */
    public void toggleTheme() {
        int currentMode = getThemeMode();
        int newMode = (currentMode == THEME_LIGHT) ? THEME_DARK : THEME_LIGHT;
        setThemeMode(newMode);
    }

    /**
     * 判断当前是否为深色主题
     */
    public boolean isDarkMode() {
        return getThemeMode() == THEME_DARK;
    }

    /**
     * 获取主题名称
     */
    public String getThemeName(int themeMode) {
        switch (themeMode) {
            case THEME_LIGHT:
                return "浅色主题";
            case THEME_DARK:
                return "深色主题";
            case THEME_SYSTEM:
                return "跟随系统";
            default:
                return "未知";
        }
    }

    /**
     * 获取当前主题名称
     */
    public String getCurrentThemeName() {
        return getThemeName(getThemeMode());
    }
}
