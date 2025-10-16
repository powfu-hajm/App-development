package com.example.qxb;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView; // 改为 CardView

public class HomeFragment extends Fragment {

    private CardView cardChat, cardDiary, cardTest; // 改为 CardView

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 确保使用正确的布局文件名称
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        // 使用正确的ID
        cardChat = view.findViewById(R.id.card_chat);
        cardDiary = view.findViewById(R.id.card_diary);
        cardTest = view.findViewById(R.id.card_test);
    }

    private void setupClickListeners() {
        if (cardChat != null) {
            cardChat.setOnClickListener(v -> {
                // 切换到AI聊天界面
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).switchToChat();
                }
            });
        }

        if (cardDiary != null) {
            cardDiary.setOnClickListener(v -> {
                // 切换到日记界面
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).switchToDiary();
                }
            });
        }

        if (cardTest != null) {
            cardTest.setOnClickListener(v -> {
                // 切换到测试界面
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).switchToTest();
                }
            });
        }
    }
}