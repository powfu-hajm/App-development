package com.example.qxb;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.qxb.models.network.ApiResponse;
import com.example.qxb.models.network.Diary;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiaryFragment extends Fragment {

    private ChipGroup chipGroupMoods, chipGroupReasons;
    private EditText etDiaryContent;
    private TextView tvCharCount;
    private Button btnSaveDiary;

    private List<String> selectedMoods = new ArrayList<>();
    private List<String> selectedReasons = new ArrayList<>();

    private ApiService apiService;
    private static final Long DEFAULT_USER_ID = 1L;

    // 添加日志标签
    private static final String TAG = "DiaryFragment";

    // 添加一个标志来跟踪是否应该自动跳转
    private boolean shouldAutoNavigate = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // 启用选项菜单
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);

        // 强制输出日志
        Log.i(TAG, "DiaryFragment onCreateView 开始");

        // 设置工具栏
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(view.findViewById(R.id.toolbar));
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("心情日记");
        }

        // 初始化ApiService - 修复空指针问题
        apiService = RetrofitClient.getApiService();
        if (apiService == null) {
            showSafeToast("网络服务初始化失败");
        }

        initViews(view);
        setupClickListeners();
        setupTextWatcher();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.diary_menu, menu);

        // 设置菜单文字颜色为黑色
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spanString = new SpannableString(item.getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spanString.length(), 0);
            item.setTitle(spanString);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_history) {
            // 跳转到历史日记
            navigateToDiaryList();
            return true;
        } else if (id == R.id.menu_chart) {
            // 跳转到情绪图表
            Intent intent = new Intent(getActivity(), MoodChartActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews(View view) {
        chipGroupMoods = view.findViewById(R.id.chipGroupMoods);
        chipGroupReasons = view.findViewById(R.id.chipGroupReasons);
        etDiaryContent = view.findViewById(R.id.etDiaryContent);
        tvCharCount = view.findViewById(R.id.tvCharCount);
        btnSaveDiary = view.findViewById(R.id.btnSaveDiary);

        Log.i(TAG, "initViews 完成");
    }

    private void setupClickListeners() {
        // 心情选择监听
        if (chipGroupMoods != null) {
            chipGroupMoods.setOnCheckedStateChangeListener((group, checkedIds) -> {
                selectedMoods.clear();
                for (int id : checkedIds) {
                    Chip chip = group.findViewById(id);
                    if (chip != null) {
                        selectedMoods.add(chip.getText().toString());
                    }
                }

                if (selectedMoods.size() > 2) {
                    Chip lastChip = group.findViewById(checkedIds.get(checkedIds.size() - 1));
                    if (lastChip != null) {
                        lastChip.setChecked(false);
                        showSafeToast(getString(R.string.max_moods));
                    }
                }
                Log.i(TAG, "心情选择: " + selectedMoods);
            });
        }

        // 原因选择监听
        if (chipGroupReasons != null) {
            chipGroupReasons.setOnCheckedStateChangeListener((group, checkedIds) -> {
                selectedReasons.clear();
                for (int id : checkedIds) {
                    Chip chip = group.findViewById(id);
                    if (chip != null) {
                        selectedReasons.add(chip.getText().toString());
                    }
                }

                if (selectedReasons.size() > 3) {
                    Chip lastChip = group.findViewById(checkedIds.get(checkedIds.size() - 1));
                    if (lastChip != null) {
                        lastChip.setChecked(false);
                        showSafeToast(getString(R.string.max_reasons));
                    }
                }
                Log.i(TAG, "原因选择: " + selectedReasons);
            });
        }

        if (btnSaveDiary != null) {
            btnSaveDiary.setOnClickListener(v -> {
                Log.i(TAG, "保存按钮被点击");
                saveDiary();
            });
        }

        Log.i(TAG, "setupClickListeners 完成");
    }

    private void setupTextWatcher() {
        if (etDiaryContent != null && tvCharCount != null) {
            etDiaryContent.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    tvCharCount.setText(s.length() + "/45");

                    if (s.length() > 45) {
                        etDiaryContent.setText(s.subSequence(0, 45));
                        etDiaryContent.setSelection(45);
                        showSafeToast(getString(R.string.max_characters));
                    }
                }
            });
        }
        Log.i(TAG, "setupTextWatcher 完成");
    }

    private void saveDiary() {
        // 检查网络服务是否可用
        if (apiService == null) {
            showSafeToast("网络服务不可用，无法保存");
            return;
        }

        String content = etDiaryContent.getText().toString().trim();

        // 验证输入
        if (selectedMoods.isEmpty()) {
            Log.w(TAG, "保存失败: 未选择心情");
            showSafeToast(getString(R.string.select_at_least_one_mood));
            return;
        }

        if (content.isEmpty()) {
            Log.w(TAG, "保存失败: 日记内容为空");
            showSafeToast(getString(R.string.write_something_today));
            return;
        }

        // 创建日记对象
        String moodTag = String.join(",", selectedMoods);
        Diary diary = new Diary(DEFAULT_USER_ID, content, moodTag);

        Log.i(TAG, "准备保存日记 - 用户ID: " + diary.getUserId() + ", 内容: " + diary.getContent() + ", 心情: " + diary.getMoodTag());

        // 设置自动跳转标志
        shouldAutoNavigate = true;

        // 调用API保存日记
        saveDiaryToServer(diary);
    }

    private void saveDiaryToServer(Diary diary) {
        Log.i(TAG, "开始保存日记到服务器...");

        // 强制输出 Retrofit 调用信息
        Log.i(TAG, "API Service: " + apiService);
        Log.i(TAG, "请求数据: userId=" + diary.getUserId() + ", content=" + diary.getContent() + ", moodTag=" + diary.getMoodTag());

        Call<ApiResponse<Diary>> call = apiService.createDiary(diary);
        Log.i(TAG, "Retrofit Call 对象: " + call);

        call.enqueue(new Callback<ApiResponse<Diary>>() {
            @Override
            public void onResponse(Call<ApiResponse<Diary>> call, Response<ApiResponse<Diary>> response) {
                Log.i(TAG, "收到服务器响应");
                Log.i(TAG, "HTTP状态码: " + response.code());
                Log.i(TAG, "响应头: " + response.headers());

                // 检查Activity是否还存在，避免内存泄漏
                if (getActivity() == null || getActivity().isFinishing() || !isAdded()) {
                    Log.w(TAG, "Activity已销毁，忽略响应");
                    return;
                }

                if (response.isSuccessful()) {
                    Log.i(TAG, "HTTP响应成功");
                    ApiResponse<Diary> apiResponse = response.body();
                    if (apiResponse != null) {
                        Log.i(TAG, "API响应体解析成功");
                        Log.i(TAG, "API响应 - code: " + apiResponse.getCode() + ", message: " + apiResponse.getMessage() + ", success: " + apiResponse.isSuccess());

                        // 关键修改：使用后端返回的code判断成功，而不是硬编码200
                        if (apiResponse.isSuccess() || (apiResponse.getCode() != null && apiResponse.getCode() == 200)) {
                            Log.i(TAG, "日记保存成功");
                            if (isAdded() && getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    showSafeToast("日记保存成功");
                                    resetForm();

                                    // 新增：如果设置了自动跳转标志，则跳转到历史日记界面
                                    if (shouldAutoNavigate) {
                                        shouldAutoNavigate = false; // 重置标志
                                        navigateToDiaryList();
                                    }
                                });
                            }
                        } else {
                            String errorMsg = "保存失败 - 错误码: " + apiResponse.getCode() + ", 信息: " + apiResponse.getMessage();
                            Log.e(TAG, errorMsg);
                            if (isAdded() && getActivity() != null) {
                                getActivity().runOnUiThread(() -> showSafeToast(errorMsg));
                            }
                        }
                    } else {
                        Log.e(TAG, "API响应体为null");
                        if (isAdded() && getActivity() != null) {
                            getActivity().runOnUiThread(() -> showSafeToast("响应体为空"));
                        }
                    }
                } else {
                    // HTTP 错误（4xx, 5xx）
                    String errorMessage = "HTTP错误 " + response.code();
                    Log.e(TAG, errorMessage);
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            errorMessage += "\n错误详情: " + errorBody;
                            Log.e(TAG, "HTTP错误响应体: " + errorBody);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "读取错误响应体失败", e);
                    }

                    final String finalErrorMessage = errorMessage;
                    if (isAdded() && getActivity() != null) {
                        getActivity().runOnUiThread(() -> showSafeToast(finalErrorMessage));
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Diary>> call, Throwable t) {
                String errorMsg = "网络请求失败: " + t.getClass().getSimpleName() + " - " + t.getMessage();
                Log.e(TAG, errorMsg, t);

                if (t instanceof java.net.ConnectException) {
                    errorMsg = "连接被拒绝 - 请检查后端服务是否运行";
                } else if (t instanceof java.net.SocketTimeoutException) {
                    errorMsg = "连接超时 - 服务器未响应";
                } else if (t instanceof java.net.UnknownHostException) {
                    errorMsg = "未知主机 - 请检查URL配置";
                }

                Log.e(TAG, "最终错误信息: " + errorMsg);

                // 确保在主线程更新UI
                if (isAdded() && getActivity() != null) {
                    String finalErrorMsg = errorMsg;
                    getActivity().runOnUiThread(() -> showSafeToast(finalErrorMsg));
                }
            }
        });
    }

    // 新增：跳转到历史日记界面的方法
    private void navigateToDiaryList() {
        Intent intent = new Intent(getActivity(), DiaryListActivity.class);
        startActivity(intent);
    }

    private void resetForm() {
        Log.i(TAG, "重置表单");
        if (etDiaryContent != null) {
            etDiaryContent.setText("");
        }

        if (chipGroupMoods != null) {
            chipGroupMoods.clearCheck();
        }

        if (chipGroupReasons != null) {
            chipGroupReasons.clearCheck();
        }

        if (tvCharCount != null) {
            tvCharCount.setText("0/45");
        }

        selectedMoods.clear();
        selectedReasons.clear();
    }

    // 安全的Toast显示方法，避免Activity销毁时的崩溃
    private void showSafeToast(String message) {
        if (isAdded() && getActivity() != null && !getActivity().isFinishing()) {
            try {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "显示Toast失败: " + e.getMessage());
            }
        }
    }
}