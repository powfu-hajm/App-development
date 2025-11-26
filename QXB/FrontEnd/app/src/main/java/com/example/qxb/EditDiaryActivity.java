package com.example.qxb;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.qxb.databinding.ActivityEditDiaryBinding;
import com.example.qxb.models.network.ApiResponse;
import com.example.qxb.models.network.Diary;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditDiaryActivity extends AppCompatActivity {

    private ActivityEditDiaryBinding binding;
    private ChipGroup chipGroupMoods;
    private EditText etDiaryContent;
    private TextView tvCharCount;
    private Button btnUpdateDiary;

    private List<String> selectedMoods = new ArrayList<>();
    private ApiService apiService;
    private Diary currentDiary;

    private static final String TAG = "EditDiaryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 先设置状态栏颜色
        setStatusBarColor();

        // 然后设置布局
        binding = ActivityEditDiaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 初始化ApiService - 修复空指针问题
        apiService = RetrofitClient.getApiService();
        if (apiService == null) {
            Toast.makeText(this, "网络服务初始化失败", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 设置Toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("编辑日记");
        }

        // 获取传递过来的日记数据
        currentDiary = (Diary) getIntent().getSerializableExtra("diary");
        if (currentDiary == null) {
            Toast.makeText(this, "日记数据错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupClickListeners();
        setupTextWatcher();
        populateData();
    }

    private void initViews() {
        chipGroupMoods = binding.chipGroupMoods;
        etDiaryContent = binding.etDiaryContent;
        tvCharCount = binding.tvCharCount;
        btnUpdateDiary = binding.btnUpdateDiary;
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
                        Toast.makeText(getApplicationContext(), "最多选择2个心情", Toast.LENGTH_SHORT).show();
                    }
                }
                Log.i(TAG, "心情选择: " + selectedMoods);
            });
        }

        if (btnUpdateDiary != null) {
            btnUpdateDiary.setOnClickListener(v -> {
                Log.i(TAG, "更新按钮被点击");
                updateDiary();
            });
        }
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
                        Toast.makeText(getApplicationContext(), "最多输入45个字符", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void populateData() {
        // 填充原有数据
        if (currentDiary.getContent() != null) {
            etDiaryContent.setText(currentDiary.getContent());
            tvCharCount.setText(currentDiary.getContent().length() + "/45");
        }

        // 设置心情选择
        if (currentDiary.getMoodTag() != null) {
            String[] moods = currentDiary.getMoodTag().split(",");
            selectedMoods.clear();
            selectedMoods.addAll(Arrays.asList(moods));

            for (String mood : moods) {
                selectMoodChip(mood.trim());
            }
        }
    }

    private void selectMoodChip(String moodText) {
        for (int i = 0; i < chipGroupMoods.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupMoods.getChildAt(i);
            if (chip.getText().toString().equals(moodText)) {
                chip.setChecked(true);
                break;
            }
        }
    }

    private void updateDiary() {
        // 检查网络服务是否可用
        if (apiService == null) {
            Toast.makeText(this, "网络服务不可用，无法更新", Toast.LENGTH_SHORT).show();
            return;
        }

        String content = etDiaryContent.getText().toString().trim();

        // 验证输入
        if (selectedMoods.isEmpty()) {
            Log.w(TAG, "更新失败: 未选择心情");
            Toast.makeText(this, "请至少选择一个心情", Toast.LENGTH_SHORT).show();
            return;
        }

        if (content.isEmpty()) {
            Log.w(TAG, "更新失败: 日记内容为空");
            Toast.makeText(this, "请填写日记内容", Toast.LENGTH_SHORT).show();
            return;
        }

        // 关键修改：创建新的日记对象用于更新，不包含时间字段
        Diary updateDiary = new Diary();
        updateDiary.setId(currentDiary.getId());
        updateDiary.setUserId(currentDiary.getUserId());
        updateDiary.setContent(content);
        updateDiary.setMoodTag(String.join(",", selectedMoods));
        // 不设置 createTime 和 updateTime，让后端自动填充

        Log.i(TAG, "准备更新日记 - ID: " + updateDiary.getId() +
                ", 用户ID: " + updateDiary.getUserId() +
                ", 内容: " + updateDiary.getContent() +
                ", 心情: " + updateDiary.getMoodTag());

        // 调用API更新日记
        updateDiaryOnServer(updateDiary);
    }

    private void updateDiaryOnServer(Diary diary) {
        Log.i(TAG, "开始更新日记到服务器...");
        Log.i(TAG, "更新前的日记数据: " + diary.toString());

        Call<ApiResponse<Diary>> call = apiService.updateDiary(diary);
        call.enqueue(new Callback<ApiResponse<Diary>>() {
            @Override
            public void onResponse(Call<ApiResponse<Diary>> call, Response<ApiResponse<Diary>> response) {
                Log.i(TAG, "收到服务器响应");
                Log.i(TAG, "HTTP状态码: " + response.code());

                if (response.isSuccessful()) {
                    Log.i(TAG, "HTTP响应成功");
                    ApiResponse<Diary> apiResponse = response.body();
                    if (apiResponse != null) {
                        Log.i(TAG, "API响应体解析成功");
                        Log.i(TAG, "API响应 - code: " + apiResponse.getCode() + ", message: " + apiResponse.getMessage() + ", success: " + apiResponse.isSuccess());

                        if (apiResponse.isSuccess()) {
                            Diary updatedDiary = apiResponse.getData();
                            if (updatedDiary != null) {
                                Log.i(TAG, "日记更新成功");
                                // 详细日志输出时间字段
                                Log.i(TAG, "=== 时间字段详情 ===");
                                Log.i(TAG, "createTime: " + updatedDiary.getCreateTime());
                                Log.i(TAG, "updateTime: " + updatedDiary.getUpdateTime());
                                Log.i(TAG, "createTime 类型: " + (updatedDiary.getCreateTime() != null ? updatedDiary.getCreateTime().getClass().getSimpleName() : "null"));
                                Log.i(TAG, "updateTime 类型: " + (updatedDiary.getUpdateTime() != null ? updatedDiary.getUpdateTime().getClass().getSimpleName() : "null"));
                                Log.i(TAG, "更新后的完整日记数据: " + updatedDiary.toString());

                                // 更新当前日记对象
                                currentDiary = updatedDiary;

                                Toast.makeText(EditDiaryActivity.this, "日记更新成功", Toast.LENGTH_SHORT).show();

                                // 设置结果并关闭
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("updatedDiary", updatedDiary);
                                setResult(RESULT_OK, resultIntent);
                                finish();
                            } else {
                                Log.e(TAG, "更新后的日记数据为null");
                                Toast.makeText(EditDiaryActivity.this, "更新后的数据为空", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String errorMsg = "更新失败 - 错误码: " + apiResponse.getCode() + ", 信息: " + apiResponse.getMessage();
                            Log.e(TAG, errorMsg);
                            Toast.makeText(EditDiaryActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.e(TAG, "API响应体为null");
                        Toast.makeText(EditDiaryActivity.this, "响应体为空", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(EditDiaryActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Diary>> call, Throwable t) {
                String errorMsg = "网络请求失败: " + t.getClass().getSimpleName() + " - " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                Toast.makeText(EditDiaryActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
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
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                window.getDecorView().setSystemUiVisibility(flags);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}