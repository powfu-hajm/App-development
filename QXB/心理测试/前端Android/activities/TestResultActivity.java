package com.example.qxb;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.qxb.models.test.TestResult;

public class TestResultActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView ivResultIcon;
    private TextView tvResultTitle;
    private TextView tvScore;
    private TextView tvPaperTitle;
    private TextView tvResultDescription;
    private TextView tvSuggestion;
    private Button btnBackToHome;

    private TestResult testResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        initViews();
        setupToolbar();

        testResult = (TestResult) getIntent().getSerializableExtra("test_result");
        if (testResult == null) {
            Toast.makeText(this, "数据错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        displayResult();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        ivResultIcon = findViewById(R.id.ivResultIcon);
        tvResultTitle = findViewById(R.id.tvResultTitle);
        tvScore = findViewById(R.id.tvScore);
        tvPaperTitle = findViewById(R.id.tvPaperTitle);
        tvResultDescription = findViewById(R.id.tvResultDescription);
        tvSuggestion = findViewById(R.id.tvSuggestion);
        btnBackToHome = findViewById(R.id.btnBackToHome);

        btnBackToHome.setOnClickListener(v -> finish());
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void displayResult() {
        tvResultTitle.setText(testResult.getResultTitle());
        tvScore.setText(String.valueOf(testResult.getTotalScore()));
        tvPaperTitle.setText(testResult.getPaperTitle());
        tvResultDescription.setText(testResult.getResultDescription());
        tvSuggestion.setText(testResult.getSuggestion());

        // 根据结果等级设置图标和颜色
        String level = testResult.getResultLevel();
        int iconRes;
        int colorRes;

        switch (level) {
            case "normal":
                iconRes = R.drawable.ic_result_normal;
                colorRes = R.color.result_normal;
                break;
            case "mild":
                iconRes = R.drawable.ic_result_mild;
                colorRes = R.color.result_mild;
                break;
            case "moderate":
                iconRes = R.drawable.ic_result_moderate;
                colorRes = R.color.result_moderate;
                break;
            case "severe":
                iconRes = R.drawable.ic_result_severe;
                colorRes = R.color.result_severe;
                break;
            default:
                iconRes = R.drawable.ic_result_normal;
                colorRes = R.color.result_normal;
        }

        ivResultIcon.setImageResource(iconRes);
        tvScore.setTextColor(ContextCompat.getColor(this, colorRes));
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
