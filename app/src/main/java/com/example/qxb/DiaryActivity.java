package com.example.qxb;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import java.util.Date;

public class DiaryActivity extends AppCompatActivity {

    private DiaryViewModel diaryViewModel;
    private EditText diaryContent;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 先注释掉setContentView，等XML修复后再取消注释
        // setContentView(R.layout.activity_diary_fragment);

        // 使用临时UI让项目先编译通过
        initializeSimpleUI();

        // 初始化ViewModel
        diaryViewModel = new ViewModelProvider(this).get(DiaryViewModel.class);
    }

    /**
     * 临时初始化简单UI
     */
    private void initializeSimpleUI() {
        // 创建简单的布局
        androidx.appcompat.widget.LinearLayoutCompat layout = new androidx.appcompat.widget.LinearLayoutCompat(this);
        layout.setOrientation(androidx.appcompat.widget.LinearLayoutCompat.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        // 日记内容输入框
        diaryContent = new EditText(this);
        diaryContent.setHint("写下您今天的心情...");
        diaryContent.setMinHeight(300);
        diaryContent.setPadding(20, 20, 20, 20);
        layout.addView(diaryContent);

        // 保存按钮
        saveButton = new Button(this);
        saveButton.setText("保存日记");
        saveButton.setPadding(20, 20, 20, 20);
        layout.addView(saveButton);

        setContentView(layout);

        // 设置点击事件
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = diaryContent.getText().toString().trim();
                if (!content.isEmpty()) {
                    diaryViewModel.saveDiary(content, new Date());
                    diaryContent.setText("");
                    showToast("日记保存成功");
                } else {
                    showToast("请输入日记内容");
                }
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}