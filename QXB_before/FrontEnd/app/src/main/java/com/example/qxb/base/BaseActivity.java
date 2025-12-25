package com.example.qxb.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qxb.R;

/**
 * 基础Activity类
 * 提供统一的页面过渡动画和通用功能
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 进入页面动画
        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
    }

    @Override
    public void finish() {
        super.finish();
        // 退出页面动画
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_left);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 返回时的动画
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_left);
    }

    /**
     * 使用缩放动画进入
     */
    protected void applyScaleEnterAnimation() {
        overridePendingTransition(R.anim.scale_in, R.anim.fade_out);
    }

    /**
     * 使用底部滑入动画
     */
    protected void applyBottomEnterAnimation() {
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.fade_out);
    }
}
