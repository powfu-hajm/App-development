package com.example.qxb;

import com.example.qxb.databinding.ActivityDiaryListBinding;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qxb.adapter.DiaryAdapter;
import com.example.qxb.models.network.ApiService;
import com.example.qxb.models.network.RetrofitClient;
import com.example.qxb.models.network.ApiResponse;
import com.example.qxb.models.entity.Diary;

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

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("历史日记");

        apiService = RetrofitClient.getApiService();
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
        apiService.getDiaries(DEFAULT_USER_ID).enqueue(new Callback<ApiResponse<List<Diary>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Diary>>> call, Response<ApiResponse<List<Diary>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    diaryList.clear();
                    diaryList.addAll(response.body().getData());
                    diaryAdapter.notifyDataSetChanged();

                    if (diaryList.isEmpty()) {
                        Toast.makeText(DiaryListActivity.this, "暂无日记", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DiaryListActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Diary>>> call, Throwable t) {
                Toast.makeText(DiaryListActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

                    // 如果列表为空，显示提示
                    if (diaryList.isEmpty()) {
                        Toast.makeText(DiaryListActivity.this, "暂无日记", Toast.LENGTH_SHORT).show();
                    }
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
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));

            // 设置状态栏文字颜色为浅色（白色）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int flags = window.getDecorView().getSystemUiVisibility();
                flags |= android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                window.getDecorView().setSystemUiVisibility(flags);
            }
        }
    }
}