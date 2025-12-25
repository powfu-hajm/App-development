package com.example.qxb;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.qxb.models.network.ApiResponse;
import com.example.qxb.models.test.TestPaper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestFragment extends Fragment {

    private Button btnMbtiTest, btnDepressionTest, btnAnxietyTest, btnStressTest;
    private ApiService apiService;
    private List<TestPaper> paperList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        apiService = RetrofitClient.getApiService();
        initViews(view);
        setupClickListeners();
        loadTestPapers();

        return view;
    }

    private void initViews(View view) {
        btnMbtiTest = view.findViewById(R.id.btnMbtiTest);
        btnDepressionTest = view.findViewById(R.id.btnDepressionTest);
        btnAnxietyTest = view.findViewById(R.id.btnAnxietyTest);
        btnStressTest = view.findViewById(R.id.btnStressTest);
    }

    private void setupClickListeners() {
        // MBTI测试（paperId=4）
        btnMbtiTest.setOnClickListener(v -> {
            startTestActivity(4L);
        });

        // 抑郁自评量表（paperId=1）
        btnDepressionTest.setOnClickListener(v -> {
            startTestActivity(1L);
        });

        // 焦虑自评量表（paperId=2）
        btnAnxietyTest.setOnClickListener(v -> {
            startTestActivity(2L);
        });

        // 压力评估测试（paperId=3）
        btnStressTest.setOnClickListener(v -> {
            startTestActivity(3L);
        });
    }

    private void loadTestPapers() {
        apiService.getTestPapers().enqueue(new Callback<ApiResponse<List<TestPaper>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TestPaper>>> call,
                                   Response<ApiResponse<List<TestPaper>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    paperList = response.body().getData();
                    // 可以根据 paperList 动态更新UI（未来扩展）
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<TestPaper>>> call, Throwable t) {
                // 网络错误时静默处理，用户仍可使用硬编码的按钮
            }
        });
    }

    private void startTestActivity(Long paperId) {
        Intent intent = new Intent(getActivity(), TestQuestionActivity.class);
        intent.putExtra("paper_id", paperId);
        startActivity(intent);
    }
}
