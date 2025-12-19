package com.example.qxb.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.qxb.R;

/**
 * 主题管理器
 * 负责管理应用的主题切换和持久化存储
 */
public class ThemeManager {

    private static final String PREF_NAME = "QXB_Theme";
    private static final String KEY_THEME = "selected_theme";

    // 主题类型常量
    public static final int THEME_BLUE = 0;  // 蓝色主题（默认）
    public static final int THEME_DARK = 1;  // 黑色主题
    public static final int THEME_PINK = 2;  // 粉色主题（樱花粉）

    private SharedPreferences prefs;
    private Context context;

    public ThemeManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 保存主题选择
     * @param themeType 主题类型 (THEME_BLUE, THEME_DARK, THEME_PINK)
     */
    public void saveTheme(int themeType) {
        prefs.edit().putInt(KEY_THEME, themeType).apply();
    }

    /**
     * 获取当前主题类型
     * @return 当前主题类型，默认为蓝色
     */
    public int getCurrentTheme() {
        return prefs.getInt(KEY_THEME, THEME_BLUE);
    }

    /**
     * 应用主题到Activity
     * 必须在 setContentView() 之前调用
     * @param activity 目标Activity
     */
    public void applyTheme(Activity activity) {
        int themeResId = getThemeResId(getCurrentTheme());
        activity.setTheme(themeResId);
    }

    /**
     * 根据主题类型获取对应的资源ID
     * @param themeType 主题类型
     * @return 主题资源ID
     */
    public static int getThemeResId(int themeType) {
        switch (themeType) {
            case THEME_DARK:
                return R.style.Theme_QXB_Dark;
            case THEME_PINK:
                return R.style.Theme_QXB_Pink;
            case THEME_BLUE:
            default:
                return R.style.Theme_QXB_Blue;
        }
    }

    /**
     * 获取主题名称
     * @param themeType 主题类型
     * @return 主题名称字符串
     */
    public static String getThemeName(int themeType) {
        switch (themeType) {
            case THEME_DARK:
                return "黑色";
            case THEME_PINK:
                return "粉色";
            case THEME_BLUE:
            default:
                return "蓝色";
        }
    }

    /**
     * 获取所有主题名称数组
     * @return 主题名称数组
     */
    public static String[] getThemeNames() {
        return new String[]{"蓝色 (默认)", "黑色", "粉色 (樱花粉)"};
    }

    /**
     * 重启Activity以应用新主题
     * @param activity 当前Activity
     */
    public static void restartActivity(Activity activity) {
        Intent intent = activity.getIntent();
        activity.finish();
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        activity.startActivity(intent);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * 检查是否为深色主题
     * @return true 如果当前是深色主题
     */
    public boolean isDarkTheme() {
        return getCurrentTheme() == THEME_DARK;
    }

    /**
     * 检查是否为粉色主题
     * @return true 如果当前是粉色主题
     */
    public boolean isPinkTheme() {
        return getCurrentTheme() == THEME_PINK;
    }
}
