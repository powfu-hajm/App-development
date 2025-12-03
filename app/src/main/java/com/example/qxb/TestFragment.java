package com.example.qxb;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;

public class TestFragment extends Fragment {

    private Button btnMbtiTest, btnDepressionTest, btnAnxietyTest, btnStressTest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        initViews(view);
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        // 绑定四个测试按钮 - 使用您布局文件中定义的ID
        btnMbtiTest = view.findViewById(R.id.btnMbtiTest);
        btnDepressionTest = view.findViewById(R.id.btnDepressionTest);
        btnAnxietyTest = view.findViewById(R.id.btnAnxietyTest);
        btnStressTest = view.findViewById(R.id.btnStressTest);
    }

    private void setupClickListeners() {
        // MBTI测试按钮点击事件
        if (btnMbtiTest != null) {
            btnMbtiTest.setOnClickListener(v -> {
                startTestActivity(
                        "MBTI人格测试",
                        "探索你的性格类型，了解职业倾向和人际关系模式",
                        "93题·约15分钟"
                );
            });
        }

        // 抑郁测试按钮点击事件
        if (btnDepressionTest != null) {
            btnDepressionTest.setOnClickListener(v -> {
                startTestActivity(
                        "抑郁自评量表",
                        "评估近两周的情绪状态，了解抑郁风险程度",
                        "20题·约5分钟"
                );
            });
        }

        // 焦虑测试按钮点击事件
        if (btnAnxietyTest != null) {
            btnAnxietyTest.setOnClickListener(v -> {
                startTestActivity(
                        "焦虑自评量表",
                        "检测焦虑症状，了解焦虑程度和影响因素",
                        "20题·约5分钟"
                );
            });
        }

        // 压力测试按钮点击事件
        if (btnStressTest != null) {
            btnStressTest.setOnClickListener(v -> {
                startTestActivity(
                        "压力评估测试",
                        "评估当前压力水平，提供减压建议和方法",
                        "15题·约4分钟"
                );
            });
        }
    }

    private void startTestActivity(String testName, String testDescription, String testDuration) {
        Intent intent = new Intent(getActivity(), TestActivity.class);
        intent.putExtra("test_name", testName);
        intent.putExtra("test_description", testDescription);
        intent.putExtra("test_duration", testDuration);
        startActivity(intent);

        // 添加页面切换动画（可选）
        if (getActivity() != null) {
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }
}