package com.example.qxb;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置布局文件 - 需要创建对应的 XML 布局
        // setContentView(R.layout.activity_main);

        // 临时显示简单的文本视图
        // 在实际项目中，应该使用 setContentView 加载布局文件
        showTemporaryView();
    }

    private void showTemporaryView() {
        // 这里只是一个临时实现
        // 在实际项目中，你应该创建 activity_main.xml 布局文件
        android.widget.TextView textView = new android.widget.TextView(this);
        textView.setText("欢迎使用青心伴！\n\n您的贴心心理健康伴侣");
        textView.setTextSize(20);
        textView.setTextColor(getResources().getColor(android.R.color.black));
        textView.setGravity(android.view.Gravity.CENTER);
        textView.setPadding(50, 50, 50, 50);
        textView.setLineSpacing(10, 1.2f);

        setContentView(textView);
    }

    // 底部导航栏相关方法
    private void setupBottomNavigation() {
        // 实现底部导航栏的逻辑
        // 需要创建对应的菜单资源和布局
    }

    // 其他 Activity 生命周期方法
    @Override
    protected void onResume() {
        super.onResume();
        // Activity 恢复时的逻辑
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Activity 暂停时的逻辑
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清理资源
    }
}