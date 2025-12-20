package com.example.qxb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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

public class TestActivity extends AppCompatActivity {

    private static final String TAG = "TestActivity";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout layoutEmpty;
    private TestHistoryAdapter adapter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        initViews();
        setupToolbar();
        setupRecyclerView();

        apiService = RetrofitClient.getApiService(this);
        loadTestHistory();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        layoutEmpty = findViewById(R.id.layoutEmpty);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new TestHistoryAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(result -> {
            // 点击查看测试结果详情
            Intent intent = new Intent(TestActivity.this, TestResultActivity.class);
            intent.putExtra("test_result", result);
            startActivity(intent);
        });
    }

    private void loadTestHistory() {
        if (apiService == null) {
            showEmpty();
            Toast.makeText(this, "网络服务初始化失败", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading();
        Log.d(TAG, "开始加载测试历史...");
        Log.d(TAG, "当前Token: " + (RetrofitClient.authToken != null ? "已设置" : "未设置"));

        apiService.getTestHistory().enqueue(new Callback<ApiResponse<List<TestResult>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TestResult>>> call,
                                   Response<ApiResponse<List<TestResult>>> response) {
                Log.d(TAG, "响应码: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "响应成功: " + response.body().isSuccess());
                    Log.d(TAG, "响应消息: " + response.body().getMessage());

                    if (response.body().isSuccess() && response.body().getData() != null) {
                        List<TestResult> data = response.body().getData();
                        Log.d(TAG, "测试历史数量: " + data.size());

                        if (data.isEmpty()) {
                            showEmpty();
                        } else {
                            showContent(data);
                        }
                    } else {
                        showEmpty();
                        String msg = response.body().getMessage();
                        if (msg != null && !msg.isEmpty()) {
                            Toast.makeText(TestActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    showEmpty();
                    Log.e(TAG, "请求失败，响应码: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "错误内容: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "读取错误内容失败", e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<TestResult>>> call, Throwable t) {
                showEmpty();
                Log.e(TAG, "网络错误", t);
                Toast.makeText(TestActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
    }

    private void showEmpty() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
    }

    private void showContent(List<TestResult> data) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
        adapter.setData(data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 每次返回页面时刷新数据
        if (apiService != null) {
            loadTestHistory();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
