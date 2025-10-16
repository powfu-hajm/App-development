package com.example.qxb;

import android.os.Bundle;
import android.view.View; // 添加这行import
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class ProfileActivity extends AppCompatActivity {

    private ProfileViewModel profileViewModel;
    private TextView tvUsername;
    private EditText etNickname;
    private Button btnSave;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 先注释掉setContentView，等XML修复后再取消注释
        // setContentView(R.layout.activity_profile_fragment);

        // 使用临时UI让项目先编译通过
        initializeSimpleUI();

        // 初始化ViewModel
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
    }

    /**
     * 临时初始化简单UI
     */
    private void initializeSimpleUI() {
        // 创建简单的布局
        androidx.appcompat.widget.LinearLayoutCompat layout = new androidx.appcompat.widget.LinearLayoutCompat(this);
        layout.setOrientation(androidx.appcompat.widget.LinearLayoutCompat.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        // 用户名显示
        TextView title = new TextView(this);
        title.setText("个人资料页面");
        title.setTextSize(20);
        layout.addView(title);

        // 昵称输入框
        etNickname = new EditText(this);
        etNickname.setHint("请输入昵称");
        etNickname.setPadding(20, 20, 20, 20);
        layout.addView(etNickname);

        // 保存按钮
        btnSave = new Button(this);
        btnSave.setText("保存昵称");
        btnSave.setPadding(20, 20, 20, 20);
        layout.addView(btnSave);

        // 退出按钮
        btnLogout = new Button(this);
        btnLogout.setText("退出登录");
        btnLogout.setPadding(20, 20, 20, 20);
        layout.addView(btnLogout);

        setContentView(layout);

        // 设置点击事件
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = etNickname.getText().toString().trim();
                if (!nickname.isEmpty()) {
                    profileViewModel.updateNickname(nickname);
                    showToast("昵称更新成功");
                } else {
                    showToast("请输入昵称");
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileViewModel.logout();
                finish();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}