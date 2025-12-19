package com.example.qxb.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.example.qxb.R;
import com.google.android.material.button.MaterialButton;

/**
 * 空状态视图组件
 * 用于显示列表为空、网络错误等状态
 */
public class EmptyStateView extends LinearLayout {

    private ImageView ivIllustration;
    private TextView tvTitle;
    private TextView tvDescription;
    private MaterialButton btnAction;

    public EmptyStateView(Context context) {
        super(context);
        init(context);
    }

    public EmptyStateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EmptyStateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        setGravity(android.view.Gravity.CENTER);

        LayoutInflater.from(context).inflate(R.layout.view_empty_state, this, true);

        ivIllustration = findViewById(R.id.ivIllustration);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        btnAction = findViewById(R.id.btnAction);
    }

    /**
     * 设置插画资源
     */
    public void setIllustration(@DrawableRes int resId) {
        if (ivIllustration != null) {
            ivIllustration.setImageResource(resId);
        }
    }

    /**
     * 设置标题
     */
    public void setTitle(String title) {
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    /**
     * 设置描述文字
     */
    public void setDescription(String description) {
        if (tvDescription != null) {
            tvDescription.setText(description);
        }
    }

    /**
     * 设置操作按钮
     */
    public void setAction(String text, OnClickListener listener) {
        if (btnAction != null) {
            btnAction.setText(text);
            btnAction.setOnClickListener(listener);
            btnAction.setVisibility(VISIBLE);
        }
    }

    /**
     * 隐藏操作按钮
     */
    public void hideAction() {
        if (btnAction != null) {
            btnAction.setVisibility(GONE);
        }
    }

    /**
     * 设置预设空状态类型
     */
    public void setEmptyType(EmptyType type) {
        switch (type) {
            case NO_DIARY:
                setIllustration(R.drawable.il_empty_diary);
                setTitle("还没有日记");
                setDescription("记录今天的心情吧");
                break;
            case NO_CHAT:
                setIllustration(R.drawable.il_empty_chat);
                setTitle("暂无聊天记录");
                setDescription("开始和AI聊聊天吧");
                break;
            case NO_TEST:
                setIllustration(R.drawable.il_empty_test);
                setTitle("还没有测试记录");
                setDescription("完成一个心理测试吧");
                break;
            case NO_ARTICLE:
                setIllustration(R.drawable.il_empty_article);
                setTitle("暂无文章");
                setDescription("稍后再来看看吧");
                break;
            case NETWORK_ERROR:
                setIllustration(R.drawable.il_network_error);
                setTitle("网络连接失败");
                setDescription("请检查网络后重试");
                break;
            case GENERAL_EMPTY:
            default:
                setIllustration(R.drawable.il_empty_general);
                setTitle("暂无内容");
                setDescription("这里空空如也");
                break;
        }
        hideAction();
    }

    /**
     * 空状态类型枚举
     */
    public enum EmptyType {
        NO_DIARY,       // 暂无日记
        NO_CHAT,        // 暂无聊天记录
        NO_TEST,        // 暂无测试记录
        NO_ARTICLE,     // 暂无文章
        NETWORK_ERROR,  // 网络错误
        GENERAL_EMPTY   // 通用空状态
    }
}
