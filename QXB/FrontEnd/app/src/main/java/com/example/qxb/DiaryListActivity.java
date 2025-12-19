package com.example.qxb;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.qxb.databinding.ActivityDiaryListBinding;
import com.example.qxb.adapter.DiaryAdapter;
import com.example.qxb.models.network.ApiResponse;
import com.example.qxb.models.network.Diary;
import com.example.qxb.widgets.EmptyStateView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiaryListActivity extends AppCompatActivity {

    private ActivityDiaryListBinding binding;
    private DiaryAdapter diaryAdapter;
    private List<Diary> diaryList = new ArrayList<>();
    private ApiService apiService;
    private static final Long DEFAULT_USER_ID = 1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor();
        binding = ActivityDiaryListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 初始化ApiService - 修复空指针问题
        apiService = RetrofitClient.getApiService();
        if (apiService == null) {
            Toast.makeText(this, "网络服务初始化失败", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 修复：直接使用布局中的Toolbar
        if (getSupportActionBar() == null) {
            setSupportActionBar(binding.toolbar);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("历史日记");
        }

        setupRecyclerView();
        loadDiaries();
    }

    private void setupRecyclerView() {
        diaryAdapter = new DiaryAdapter(diaryList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(diaryAdapter);

        // 设置删除按钮点击监听器
        diaryAdapter.setOnDeleteClickListener(new DiaryAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(int position) {
                deleteDiary(position);
            }
        });

        // 设置编辑按钮点击监听器
        diaryAdapter.setOnEditClickListener(new DiaryAdapter.OnEditClickListener() {
            @Override
            public void onEditClick(int position) {
                editDiary(position);
            }
        });
    }

    private void loadDiaries() {
        if (apiService == null) {
            Toast.makeText(this, "网络服务不可用", Toast.LENGTH_SHORT).show();
            showNetworkError();
            return;
        }

        apiService.getDiaries(DEFAULT_USER_ID).enqueue(new Callback<ApiResponse<List<Diary>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Diary>>> call, Response<ApiResponse<List<Diary>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    diaryList.clear();
                    diaryList.addAll(response.body().getData());
                    diaryAdapter.notifyDataSetChanged();

                    updateEmptyState();
                } else {
                    Toast.makeText(DiaryListActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    showNetworkError();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Diary>>> call, Throwable t) {
                Toast.makeText(DiaryListActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                showNetworkError();
            }
        });
    }

    /**
     * 更新空状态显示
     */
    private void updateEmptyState() {
        if (diaryList.isEmpty()) {
            binding.recyclerView.setVisibility(android.view.View.GONE);
            binding.emptyStateView.setVisibility(android.view.View.VISIBLE);
            binding.emptyStateView.setEmptyType(EmptyStateView.EmptyType.NO_DIARY);
            binding.emptyStateView.setAction("写日记", v -> {
                finish(); // 返回主页写日记
            });
        } else {
            binding.recyclerView.setVisibility(android.view.View.VISIBLE);
            binding.emptyStateView.setVisibility(android.view.View.GONE);
        }
    }

    /**
     * 显示网络错误状态
     */
    private void showNetworkError() {
        binding.recyclerView.setVisibility(android.view.View.GONE);
        binding.emptyStateView.setVisibility(android.view.View.VISIBLE);
        binding.emptyStateView.setEmptyType(EmptyStateView.EmptyType.NETWORK_ERROR);
        binding.emptyStateView.setAction("重试", v -> loadDiaries());
    }

    private void deleteDiary(int position) {
        if (position < 0 || position >= diaryList.size()) {
            return;
        }

        Diary diary = diaryList.get(position);
        Long diaryId = diary.getId();

        if (diaryId == null) {
            Toast.makeText(this, "无法删除：日记ID为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (apiService == null) {
            Toast.makeText(this, "网络服务不可用", Toast.LENGTH_SHORT).show();
            return;
        }

        // 调用删除API
        apiService.deleteDiary(diaryId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // 从列表中移除
                    diaryList.remove(position);
                    diaryAdapter.notifyItemRemoved(position);
                    Toast.makeText(DiaryListActivity.this, "删除成功", Toast.LENGTH_SHORT).show();

                    // 新增：发送广播通知图表页面数据已更新
                    sendDataUpdateBroadcast();

                    // 更新空状态显示
                    updateEmptyState();
                } else {
                    String errorMsg = "删除失败";
                    if (response.body() != null) {
                        errorMsg += ": " + response.body().getMessage();
                    }
                    Toast.makeText(DiaryListActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(DiaryListActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 新增方法：发送数据更新广播
    private void sendDataUpdateBroadcast() {
        Intent intent = new Intent("DIARY_DATA_UPDATED");
        sendBroadcast(intent);
    }

    private void editDiary(int position) {
        if (position < 0 || position >= diaryList.size()) {
            return;
        }

        Diary diary = diaryList.get(position);

        // 跳转到编辑界面
        Intent intent = new Intent(this, EditDiaryActivity.class);
        intent.putExtra("diary", diary);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // 重新加载日记列表以显示更新后的数据
            loadDiaries();
            // 新增：发送数据更新广播
            sendDataUpdateBroadcast();
            Toast.makeText(this, "日记更新成功", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    // 添加状态栏设置方法
    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, com.example.qxb.R.color.colorPrimary));

            // 设置状态栏文字颜色为浅色（白色）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int flags = window.getDecorView().getSystemUiVisibility();
                flags |= android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                window.getDecorView().setSystemUiVisibility(flags);
            }
        }
    }
}