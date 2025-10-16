package com.example.qxb;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 先注释掉setContentView，等XML修复后再取消注释
        // setContentView(R.layout.activity_home_fragment);

        // 使用临时UI让项目先编译通过
        initializeSimpleUI();
    }

    /**
     * 临时初始化简单UI
     */
    private void initializeSimpleUI() {
        // 创建简单的布局
        androidx.appcompat.widget.LinearLayoutCompat layout = new androidx.appcompat.widget.LinearLayoutCompat(this);
        layout.setOrientation(androidx.appcompat.widget.LinearLayoutCompat.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        // 添加提示文本
        android.widget.TextView textView = new android.widget.TextView(this);
        textView.setText("主页功能正在开发中...\n请先修复activity_home_fragment.xml文件");
        textView.setTextSize(18);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        layout.addView(textView);

        setContentView(layout);
    }
}