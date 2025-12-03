package com.example.qxb;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private Button btnLogout;
    private View menuConsultation, menuTests, menuDiaries, menuSettings;
    private View btnEditProfile;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        btnLogout = view.findViewById(R.id.btnLogout);
        menuConsultation = view.findViewById(R.id.menu_consultation);
        menuTests = view.findViewById(R.id.menu_tests);
        menuDiaries = view.findViewById(R.id.menu_diaries);
        menuSettings = view.findViewById(R.id.menu_settings);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
    }

    private void setupClickListeners() {
        btnLogout.setOnClickListener(v -> logout());

        btnEditProfile.setOnClickListener(v -> {
            Toast.makeText(getContext(), "编辑个人信息", Toast.LENGTH_SHORT).show();
        });

        menuConsultation.setOnClickListener(v -> {
            Toast.makeText(getContext(), "我的咨询记录", Toast.LENGTH_SHORT).show();
        });

        menuTests.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TestHistoryActivity.class);
            startActivity(intent);
        });

        menuDiaries.setOnClickListener(v -> {
            Toast.makeText(getContext(), "我的日记记录", Toast.LENGTH_SHORT).show();
        });

        menuSettings.setOnClickListener(v -> {
            Toast.makeText(getContext(), "应用设置", Toast.LENGTH_SHORT).show();
        });
    }

    private void logout() {
        // 清除登录状态，跳转到登录界面
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();

        Toast.makeText(getContext(), "已退出登录", Toast.LENGTH_SHORT).show();
    }
}