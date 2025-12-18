package com.example.qxb.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import android.os.Bundle;

public class HomePagerAdapter extends FragmentStateAdapter {

    public HomePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 根据位置返回对应的Fragment
        switch (position) {
            case 0:
                return createPlaceholderFragment("首页内容");     // 临时解决方案
            case 1:
                return createPlaceholderFragment("日记内容");    // 临时解决方案
            case 2:
                return createPlaceholderFragment("测试内容");     // 临时解决方案
            case 3:
                return createPlaceholderFragment("个人资料内容");  // 临时解决方案
            default:
                return createPlaceholderFragment("首页内容");
        }
    }

    @Override
    public int getItemCount() {
        return 4; // 4个标签页
    }

    private Fragment createPlaceholderFragment(String title) {
        // 使用简单的Fragment来占位
        androidx.fragment.app.Fragment fragment = new androidx.fragment.app.Fragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }
}