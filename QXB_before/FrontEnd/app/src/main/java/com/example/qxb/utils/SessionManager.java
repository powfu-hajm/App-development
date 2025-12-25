package com.example.qxb.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SessionManager {
    private static final String PREFS_NAME = "UserSession";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // 保存登录信息（用户名和token）
    public void saveLoginInfo(String username, String token) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_TOKEN, token);
        editor.apply();
        Log.d("SessionManager", "保存登录信息: username=" + username + ", token=" + (token != null ? token.substring(0, Math.min(token.length(), 10)) + "..." : "null"));
    }

    // 保存用户ID
    public void saveUserId(Long userId) {
        editor.putLong(KEY_USER_ID, userId);
        editor.apply();
        Log.d("SessionManager", "保存用户ID: " + userId);
    }

    // 获取用户ID
    public Long getUserId() {
        Long userId = prefs.getLong(KEY_USER_ID, -1L);
        Log.d("SessionManager", "获取用户ID: " + userId);
        return userId;
    }

    // 登录成功后调用，保存完整的用户信息
    public void createLoginSession(String username, Long userId, String token) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putLong(KEY_USER_ID, userId);
        editor.putString(KEY_TOKEN, token);
        editor.apply();
        Log.d("SessionManager", "创建登录会话: username=" + username + ", userId=" + userId);
    }

    // 获取用户名
    public String getUsername() {
        String username = prefs.getString(KEY_USERNAME, null);
        Log.d("SessionManager", "获取用户名: " + username);
        return username;
    }

    // 获取token
    public String getToken() {
        String token = prefs.getString(KEY_TOKEN, null);
        Log.d("SessionManager", "获取token: " + (token != null ? token.substring(0, Math.min(token.length(), 10)) + "..." : "null"));
        return token;
    }

    // 检查是否已登录
    public boolean isLoggedIn() {
        boolean loggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);
        Log.d("SessionManager", "是否已登录: " + loggedIn);
        return loggedIn;
    }

    // 退出登录
    public void logout() {
        editor.clear();
        editor.apply();
        Log.d("SessionManager", "用户已退出登录");
    }

    // 需要导入Log类
    private static final String TAG = "SessionManager";

    // 或者添加一个辅助方法
    private void log(String message) {
        android.util.Log.d(TAG, message);
    }
}