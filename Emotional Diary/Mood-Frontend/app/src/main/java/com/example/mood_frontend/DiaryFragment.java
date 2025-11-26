package com.example.mood_frontend;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mood_frontend.network.ApiService;
import com.example.mood_frontend.network.RetrofitClient;
import com.example.mood_frontend.network.model.ApiResponse;
import com.example.mood_frontend.network.model.Diary;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);

        // 强制输出日志
        Log.i(TAG, "DiaryFragment onCreateView 开始");

        apiService = RetrofitClient.getApiService();
        initViews(view);
        setupClickListeners();
        setupTextWatcher();

        return view;
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
                        Toast.makeText(getContext(), R.string.max_moods, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), R.string.max_reasons, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), R.string.max_characters, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        Log.i(TAG, "setupTextWatcher 完成");
    }

    private void saveDiary() {
        String content = etDiaryContent.getText().toString().trim();

        // 验证输入
        if (selectedMoods.isEmpty()) {
            Log.w(TAG, "保存失败: 未选择心情");
            Toast.makeText(getContext(), R.string.select_at_least_one_mood, Toast.LENGTH_SHORT).show();
            return;
        }

        if (content.isEmpty()) {
            Log.w(TAG, "保存失败: 日记内容为空");
            Toast.makeText(getContext(), R.string.write_something_today, Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建日记对象
        String moodTag = String.join(",", selectedMoods);
        Diary diary = new Diary(DEFAULT_USER_ID, content, moodTag);

        Log.i(TAG, "准备保存日记 - 用户ID: " + diary.getUserId() + ", 内容: " + diary.getContent() + ", 心情: " + diary.getMoodTag());

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
                if (getActivity() == null || getActivity().isFinishing()) {
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
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "日记保存成功", Toast.LENGTH_SHORT).show();
                                resetForm();
                            });
                        } else {
                            String errorMsg = "保存失败 - 错误码: " + apiResponse.getCode() + ", 信息: " + apiResponse.getMessage();
                            Log.e(TAG, errorMsg);
                            getActivity().runOnUiThread(() ->
                                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show());
                        }
                    } else {
                        Log.e(TAG, "API响应体为null");
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "响应体为空", Toast.LENGTH_SHORT).show());
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
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), finalErrorMessage, Toast.LENGTH_LONG).show());
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
                if (getActivity() != null) {
                    String finalErrorMsg = errorMsg;
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), finalErrorMsg, Toast.LENGTH_LONG).show());
                }
            }
        });
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
}