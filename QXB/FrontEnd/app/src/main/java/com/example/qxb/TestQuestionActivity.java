package com.example.qxb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.qxb.models.network.ApiResponse;
import com.example.qxb.models.test.TestOption;
import com.example.qxb.models.test.TestPaperDetail;
import com.example.qxb.models.test.TestQuestion;
import com.example.qxb.models.test.TestResult;
import com.example.qxb.models.test.TestSubmitRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestQuestionActivity extends AppCompatActivity {

    private static final Long DEFAULT_USER_ID = 1L;

    private Toolbar toolbar;
    private TextView tvToolbarTitle;
    private TextView tvProgress;
    private TextView tvProgressPercent;
    private ProgressBar progressBar;
    private TextView tvQuestionContent;
    private RadioGroup radioGroupOptions;
    private RadioButton optionA, optionB, optionC, optionD;
    private Button btnPrevious, btnNext;

    private ApiService apiService;
    private TestPaperDetail paperDetail;
    private int currentQuestionIndex = 0;
    private Map<Long, Long> answers = new HashMap<>(); // questionId -> optionId
    private List<RadioButton> optionButtons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_question);

        initViews();
        setupToolbar();
        apiService = RetrofitClient.getApiService();

        Long paperId = getIntent().getLongExtra("paper_id", -1);
        if (paperId == -1) {
            Toast.makeText(this, "参数错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadPaperDetail(paperId);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        tvProgress = findViewById(R.id.tvProgress);
        tvProgressPercent = findViewById(R.id.tvProgressPercent);
        progressBar = findViewById(R.id.progressBar);
        tvQuestionContent = findViewById(R.id.tvQuestionContent);
        radioGroupOptions = findViewById(R.id.radioGroupOptions);
        optionA = findViewById(R.id.optionA);
        optionB = findViewById(R.id.optionB);
        optionC = findViewById(R.id.optionC);
        optionD = findViewById(R.id.optionD);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);

        optionButtons.add(optionA);
        optionButtons.add(optionB);
        optionButtons.add(optionC);
        optionButtons.add(optionD);

        btnPrevious.setOnClickListener(v -> goToPreviousQuestion());
        btnNext.setOnClickListener(v -> goToNextQuestion());
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadPaperDetail(Long paperId) {
        apiService.getTestPaperDetail(paperId).enqueue(new Callback<ApiResponse<TestPaperDetail>>() {
            @Override
            public void onResponse(Call<ApiResponse<TestPaperDetail>> call, Response<ApiResponse<TestPaperDetail>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    paperDetail = response.body().getData();
                    tvToolbarTitle.setText(paperDetail.getTitle());
                    displayQuestion(0);
                } else {
                    Toast.makeText(TestQuestionActivity.this, "加载问卷失败", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TestPaperDetail>> call, Throwable t) {
                Toast.makeText(TestQuestionActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayQuestion(int index) {
        if (paperDetail == null || paperDetail.getQuestions() == null ||
            index < 0 || index >= paperDetail.getQuestions().size()) {
            return;
        }

        currentQuestionIndex = index;
        TestQuestion question = paperDetail.getQuestions().get(index);
        int totalQuestions = paperDetail.getQuestions().size();

        // 更新进度
        tvProgress.setText("第 " + (index + 1) + "/" + totalQuestions + " 题");
        int percent = (int) ((index + 1) * 100.0 / totalQuestions);
        tvProgressPercent.setText(percent + "%");
        progressBar.setProgress(percent);

        // 显示题目
        tvQuestionContent.setText(question.getContent());

        // 显示选项
        radioGroupOptions.clearCheck();
        List<TestOption> options = question.getOptions();
        for (int i = 0; i < optionButtons.size(); i++) {
            RadioButton btn = optionButtons.get(i);
            if (i < options.size()) {
                TestOption option = options.get(i);
                btn.setVisibility(View.VISIBLE);
                btn.setText(option.getOptionLabel() + ". " + option.getContent());
                btn.setTag(option.getId());

                // 恢复之前的选择
                if (answers.containsKey(question.getId()) &&
                    answers.get(question.getId()).equals(option.getId())) {
                    btn.setChecked(true);
                }
            } else {
                btn.setVisibility(View.GONE);
            }
        }

        // 更新按钮状态
        btnPrevious.setEnabled(index > 0);
        btnPrevious.setAlpha(index > 0 ? 1.0f : 0.5f);

        if (index == totalQuestions - 1) {
            btnNext.setText("提交");
        } else {
            btnNext.setText("下一题");
        }
    }

    private void saveCurrentAnswer() {
        if (paperDetail == null || paperDetail.getQuestions() == null) return;

        TestQuestion currentQuestion = paperDetail.getQuestions().get(currentQuestionIndex);
        int checkedId = radioGroupOptions.getCheckedRadioButtonId();

        if (checkedId != -1) {
            RadioButton checkedButton = findViewById(checkedId);
            Long optionId = (Long) checkedButton.getTag();
            answers.put(currentQuestion.getId(), optionId);
        }
    }

    private void goToPreviousQuestion() {
        saveCurrentAnswer();
        if (currentQuestionIndex > 0) {
            displayQuestion(currentQuestionIndex - 1);
        }
    }

    private void goToNextQuestion() {
        saveCurrentAnswer();

        // 检查是否选择了答案
        TestQuestion currentQuestion = paperDetail.getQuestions().get(currentQuestionIndex);
        if (!answers.containsKey(currentQuestion.getId())) {
            Toast.makeText(this, "请选择一个选项", Toast.LENGTH_SHORT).show();
            return;
        }

        int totalQuestions = paperDetail.getQuestions().size();
        if (currentQuestionIndex < totalQuestions - 1) {
            displayQuestion(currentQuestionIndex + 1);
        } else {
            // 最后一题，提交答案
            submitTest();
        }
    }

    private void submitTest() {
        // 检查是否所有题目都已回答
        if (answers.size() < paperDetail.getQuestions().size()) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("您还有 " + (paperDetail.getQuestions().size() - answers.size()) + " 道题未作答，确定要提交吗？")
                    .setPositiveButton("继续答题", null)
                    .setNegativeButton("提交", (dialog, which) -> doSubmit())
                    .show();
        } else {
            doSubmit();
        }
    }

    private void doSubmit() {
        List<Long> optionIds = new ArrayList<>(answers.values());
        TestSubmitRequest request = new TestSubmitRequest(paperDetail.getId(), DEFAULT_USER_ID, optionIds);

        // 显示加载提示
        btnNext.setEnabled(false);
        btnNext.setText("提交中...");

        apiService.submitTest(request).enqueue(new Callback<ApiResponse<TestResult>>() {
            @Override
            public void onResponse(Call<ApiResponse<TestResult>> call, Response<ApiResponse<TestResult>> response) {
                btnNext.setEnabled(true);
                btnNext.setText("提交");

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    TestResult result = response.body().getData();
                    Intent intent = new Intent(TestQuestionActivity.this, TestResultActivity.class);
                    intent.putExtra("test_result", result);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(TestQuestionActivity.this, "提交失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TestResult>> call, Throwable t) {
                btnNext.setEnabled(true);
                btnNext.setText("提交");
                Toast.makeText(TestQuestionActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("退出测试")
                .setMessage("确定要退出测试吗？当前进度将不会保存。")
                .setPositiveButton("继续测试", null)
                .setNegativeButton("退出", (dialog, which) -> finish())
                .show();
    }
}
