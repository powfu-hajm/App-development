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


public class TestFragment extends Fragment {

    private Button btnMbtiTest, btnDepressionTest, btnAnxietyTest, btnStressTest;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        initViews(view);
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        btnMbtiTest = view.findViewById(R.id.btnMbtiTest);
        btnDepressionTest = view.findViewById(R.id.btnDepressionTest);
        btnAnxietyTest = view.findViewById(R.id.btnAnxietyTest);
        btnStressTest = view.findViewById(R.id.btnStressTest);
    }

    private void setupClickListeners() {
        btnMbtiTest.setOnClickListener(v -> startTest("MBTI人格测试"));
        btnDepressionTest.setOnClickListener(v -> startTest("抑郁自评量表"));
        btnAnxietyTest.setOnClickListener(v -> startTest("焦虑自评量表"));
        btnStressTest.setOnClickListener(v -> startTest("压力评估测试"));
    }

    private void startTest(String testName) {
        Intent intent = new Intent(getActivity(), TestFragment.class);
        intent.putExtra("test_name", testName);
        startActivity(intent);

        Toast.makeText(getContext(), "开始" + testName, Toast.LENGTH_SHORT).show();
    }
}