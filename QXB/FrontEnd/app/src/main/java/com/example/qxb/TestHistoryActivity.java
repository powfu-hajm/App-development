package com.example.qxb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qxb.adapter.TestHistoryAdapter;
import com.example.qxb.models.network.ApiResponse;
import com.example.qxb.models.test.TestResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestHistoryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressBar progressBar;
    private View emptyView;
    private RecyclerView recyclerView;

    private ApiService apiService;
    private TestHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_history);

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadTestHistory();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);
        recyclerView = findViewById(R.id.recyclerView);

        apiService = RetrofitClient.getApiService();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new TestHistoryAdapter();
        adapter.setOnItemClickListener(this::onHistoryItemClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadTestHistory() {
        showLoading();

        // 使用固定的userId（后续可从用户登录状态获取）
        Long userId = 1L;

        apiService.getTestHistory(userId).enqueue(new Callback<ApiResponse<List<TestResult>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TestResult>>> call,
                                   Response<ApiResponse<List<TestResult>>> response) {
                hideLoading();

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<TestResult> historyList = response.body().getData();
                    if (historyList != null && !historyList.isEmpty()) {
                        showContent(historyList);
                    } else {
                        showEmpty();
                    }
                } else {
                    showEmpty();
                    Toast.makeText(TestHistoryActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<TestResult>>> call, Throwable t) {
                hideLoading();
                showEmpty();
                Toast.makeText(TestHistoryActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onHistoryItemClick(TestResult result) {
        Intent intent = new Intent(this, TestResultActivity.class);
        intent.putExtra("test_result", result);
        startActivity(intent);
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private void showContent(List<TestResult> list) {
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        adapter.setData(list);
    }

    private void showEmpty() {
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }
}
