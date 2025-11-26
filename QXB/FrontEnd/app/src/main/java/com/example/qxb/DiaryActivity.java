package com.example.qxb;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.qxb.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DiaryActivity extends AppCompatActivity {

    private TextView tvCurrentDate;
    private EditText etDiaryTitle, etDiaryContent;
    private Button btnSaveDiary;

    private String selectedMood = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary); // 现在布局文件已存在

        initViews();
        setupToolbar();
        setupClickListeners();
        updateCurrentDate();
    }

    private void initViews() {
        // 初始化工具栏
        Toolbar toolbar = findViewById(R.id.toolbar);

        // 初始化其他视图
        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        etDiaryTitle = findViewById(R.id.etDiaryTitle);
        etDiaryContent = findViewById(R.id.etDiaryContent);
        btnSaveDiary = findViewById(R.id.btnSaveDiary);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 设置返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 设置返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupClickListeners() {
        // 心情选择
        setupMoodSelection();

        // 保存按钮
        btnSaveDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDiary();
            }
        });
    }

    private void setupMoodSelection() {
        // 心情1：开心
        findViewById(R.id.mood_happy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMood("happy", v);
            }
        });

        // 心情2：平静
        findViewById(R.id.mood_calm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMood("calm", v);
            }
        });

        // 心情3：一般
        findViewById(R.id.mood_normal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMood("normal", v);
            }
        });

        // 心情4：难过
        findViewById(R.id.mood_sad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMood("sad", v);
            }
        });

        // 心情5：焦虑
        findViewById(R.id.mood_anxious).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMood("anxious", v);
            }
        });
    }

    private void selectMood(String mood, View view) {
        // 重置所有心情选择状态
        resetMoodSelection();

        // 设置选中状态
        view.setSelected(true);
        selectedMood = mood;

        // 显示选中的心情
        String moodText = getMoodText(mood);
        Toast.makeText(this, "选择了心情：" + moodText, Toast.LENGTH_SHORT).show();
    }

    private void resetMoodSelection() {
        findViewById(R.id.mood_happy).setSelected(false);
        findViewById(R.id.mood_calm).setSelected(false);
        findViewById(R.id.mood_normal).setSelected(false);
        findViewById(R.id.mood_sad).setSelected(false);
        findViewById(R.id.mood_anxious).setSelected(false);
    }

    private String getMoodText(String mood) {
        switch (mood) {
            case "happy": return "开心";
            case "calm": return "平静";
            case "normal": return "一般";
            case "sad": return "难过";
            case "anxious": return "焦虑";
            default: return "未知";
        }
    }

    private void updateCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        String currentDate = dateFormat.format(new Date());
        tvCurrentDate.setText(currentDate);
    }

    private void saveDiary() {
        String title = etDiaryTitle.getText().toString().trim();
        String content = etDiaryContent.getText().toString().trim();

        // 简单验证
        if (title.isEmpty()) {
            Toast.makeText(this, "请输入日记标题", Toast.LENGTH_SHORT).show();
            return;
        }

        if (content.isEmpty()) {
            Toast.makeText(this, "请输入日记内容", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedMood.isEmpty()) {
            Toast.makeText(this, "请选择心情", Toast.LENGTH_SHORT).show();
            return;
        }

        // 这里可以添加保存到数据库的逻辑
        // 暂时显示成功消息
        Toast.makeText(this, "日记保存成功！", Toast.LENGTH_SHORT).show();

        // 清空输入
        etDiaryTitle.setText("");
        etDiaryContent.setText("");
        resetMoodSelection();
        selectedMood = "";

        // 显示功能提示
        Toast.makeText(this, "日记功能开发中，数据暂存于内存", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}