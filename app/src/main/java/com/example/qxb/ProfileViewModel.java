package com.example.qxb;

import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {

    public void updateNickname(String nickname) {
        // 这里实现昵称更新逻辑
        System.out.println("更新昵称为: " + nickname);
        // 可以连接到数据库、SharedPreferences或网络API
    }

    public void logout() {
        // 这里实现退出登录逻辑
        System.out.println("用户退出登录");
        // 清除用户会话、跳转到登录页面等
    }
}