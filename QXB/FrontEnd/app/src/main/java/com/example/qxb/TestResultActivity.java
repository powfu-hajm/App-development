package com.example.qxb;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.qxb.models.test.TestResult;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class TestResultActivity extends AppCompatActivity {

    // 通用控件
    private Toolbar toolbar;
    private TextView tvToolbarTitle;
    private Button btnBackToHome;

    // 普通测试结果控件
    private LinearLayout normalResultContainer;
    private ImageView ivResultIcon;
    private TextView tvResultTitle;
    private TextView tvScore;
    private TextView tvPaperTitle;
    private TextView tvResultDescription;
    private TextView tvSuggestion;

    // MBTI 测试结果控件
    private LinearLayout mbtiResultContainer;
    private MaterialCardView cardMbtiHeader;
    private TextView tvMbtiTestTime;
    private ImageView ivMbtiTypeImage;
    private TextView tvMbtiTypeCode;
    private TextView tvMbtiTypeName;
    private TextView tvMbtiTypeNameEn;
    private TextView tvMbtiBriefDesc;
    private TextView tvMbtiDetailDesc;
    private TextView tvStrengths;
    private TextView tvWeaknesses;
    private TextView tvCareerSuggestions;
    private TextView tvFamousPeople;

    // MBTI 四维度进度条
    private View dimensionEI, dimensionSN, dimensionTF, dimensionJP;

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

        // 根据结果类型显示不同的布局
        if (testResult.getIsMbtiResult()) {
            displayMbtiResult();
        } else {
            displayNormalResult();
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        btnBackToHome = findViewById(R.id.btnBackToHome);
        btnBackToHome.setOnClickListener(v -> finish());

        // 普通测试结果控件
        normalResultContainer = findViewById(R.id.normalResultContainer);
        ivResultIcon = findViewById(R.id.ivResultIcon);
        tvResultTitle = findViewById(R.id.tvResultTitle);
        tvScore = findViewById(R.id.tvScore);
        tvPaperTitle = findViewById(R.id.tvPaperTitle);
        tvResultDescription = findViewById(R.id.tvResultDescription);
        tvSuggestion = findViewById(R.id.tvSuggestion);

        // MBTI 测试结果控件
        mbtiResultContainer = findViewById(R.id.mbtiResultContainer);
        cardMbtiHeader = findViewById(R.id.cardMbtiHeader);
        tvMbtiTestTime = findViewById(R.id.tvMbtiTestTime);
        ivMbtiTypeImage = findViewById(R.id.ivMbtiTypeImage);
        tvMbtiTypeCode = findViewById(R.id.tvMbtiTypeCode);
        tvMbtiTypeName = findViewById(R.id.tvMbtiTypeName);
        tvMbtiTypeNameEn = findViewById(R.id.tvMbtiTypeNameEn);
        tvMbtiBriefDesc = findViewById(R.id.tvMbtiBriefDesc);
        tvMbtiDetailDesc = findViewById(R.id.tvMbtiDetailDesc);
        tvStrengths = findViewById(R.id.tvStrengths);
        tvWeaknesses = findViewById(R.id.tvWeaknesses);
        tvCareerSuggestions = findViewById(R.id.tvCareerSuggestions);
        tvFamousPeople = findViewById(R.id.tvFamousPeople);

        // 四维度进度条
        dimensionEI = findViewById(R.id.dimensionEI);
        dimensionSN = findViewById(R.id.dimensionSN);
        dimensionTF = findViewById(R.id.dimensionTF);
        dimensionJP = findViewById(R.id.dimensionJP);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    /**
     * 显示普通测试结果
     */
    private void displayNormalResult() {
        normalResultContainer.setVisibility(View.VISIBLE);
        mbtiResultContainer.setVisibility(View.GONE);
        tvToolbarTitle.setText("测试结果");

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

    /**
     * 显示 MBTI 测试结果
     */
    private void displayMbtiResult() {
        normalResultContainer.setVisibility(View.GONE);
        mbtiResultContainer.setVisibility(View.VISIBLE);
        tvToolbarTitle.setText("MBTI测试结果");

        // 设置基本信息
        String mbtiType = testResult.getMbtiType();
        String typeName = testResult.getTypeName();

        tvMbtiTypeCode.setText(mbtiType != null ? mbtiType : "----");
        tvMbtiTypeName.setText(typeName != null ? typeName : "");

        // 设置测试时间
        String testTime = testResult.getTestTime();
        if (testTime != null) {
            tvMbtiTestTime.setText("生成于：" + testTime.replace("T", " ").substring(0, 19));
        }

        // 设置类型英文名称
        String typeNameEn = testResult.getTypeNameEn();
        if (typeNameEn != null) {
            tvMbtiTypeNameEn.setText(typeNameEn);
        }

        // 设置简短描述
        String briefDesc = testResult.getBriefDesc();
        if (briefDesc != null) {
            tvMbtiBriefDesc.setText(briefDesc);
        }

        // 设置详细描述
        String detailDesc = testResult.getDetailDesc();
        if (detailDesc != null) {
            tvMbtiDetailDesc.setText(detailDesc);
        }

        // 设置四维百分比
        setupDimensionBar(dimensionEI, "外向", "E", testResult.getEPercent(),
                "内向", "I", testResult.getIPercent(), getDimensionColor(0));
        setupDimensionBar(dimensionSN, "实感", "S", testResult.getSPercent(),
                "直觉", "N", testResult.getNPercent(), getDimensionColor(1));
        setupDimensionBar(dimensionTF, "理性", "T", testResult.getTPercent(),
                "感性", "F", testResult.getFPercent(), getDimensionColor(2));
        setupDimensionBar(dimensionJP, "判断", "J", testResult.getJPercent(),
                "知觉", "P", testResult.getPPercent(), getDimensionColor(3));

        // 设置优势特点
        List<String> strengths = testResult.getStrengths();
        if (strengths != null && !strengths.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String item : strengths) {
                sb.append("• ").append(item).append("\n");
            }
            tvStrengths.setText(sb.toString().trim());
        }

        // 设置潜在弱点
        List<String> weaknesses = testResult.getWeaknesses();
        if (weaknesses != null && !weaknesses.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String item : weaknesses) {
                sb.append("• ").append(item).append("\n");
            }
            tvWeaknesses.setText(sb.toString().trim());
        }

        // 设置职业建议
        List<String> careers = testResult.getCareerSuggestions();
        if (careers != null && !careers.isEmpty()) {
            tvCareerSuggestions.setText(String.join("、", careers));
        }

        // 设置著名人物
        List<String> famous = testResult.getFamousPeople();
        if (famous != null && !famous.isEmpty()) {
            tvFamousPeople.setText(String.join("、", famous));
        }

        // 设置类型图片（根据 imageName 或 mbtiType）
        setMbtiTypeImage(mbtiType);

        // 设置主题色
        String colorPrimary = testResult.getColorPrimary();
        if (colorPrimary != null) {
            try {
                int color = Color.parseColor(colorPrimary);
                tvMbtiTypeName.setTextColor(color);
                tvMbtiTypeNameEn.setTextColor(color);
                tvCareerSuggestions.setTextColor(color);
            } catch (Exception e) {
                // 使用默认颜色
            }
        }
    }

    /**
     * 设置维度进度条
     */
    private void setupDimensionBar(View dimensionView, String leftLabel, String leftCode, int leftPercent,
                                   String rightLabel, String rightCode, int rightPercent, int barColor) {
        if (dimensionView == null) return;

        TextView tvLeftLabel = dimensionView.findViewById(R.id.tvLeftLabel);
        TextView tvLeftCode = dimensionView.findViewById(R.id.tvLeftCode);
        TextView tvLeftPercent = dimensionView.findViewById(R.id.tvLeftPercent);
        TextView tvRightLabel = dimensionView.findViewById(R.id.tvRightLabel);
        TextView tvRightCode = dimensionView.findViewById(R.id.tvRightCode);
        TextView tvRightPercent = dimensionView.findViewById(R.id.tvRightPercent);
        View viewLeftProgress = dimensionView.findViewById(R.id.viewLeftProgress);
        View viewRightProgress = dimensionView.findViewById(R.id.viewRightProgress);

        // 设置标签
        tvLeftLabel.setText(leftLabel);
        tvLeftCode.setText("（" + leftCode + "）");
        tvLeftPercent.setText(leftPercent + "%");
        tvRightLabel.setText(rightLabel);
        tvRightCode.setText("（" + rightCode + "）");
        tvRightPercent.setText(rightPercent + "%");

        // 设置进度条宽度
        FrameLayout parentBar = (FrameLayout) viewLeftProgress.getParent();
        parentBar.post(() -> {
            int totalWidth = parentBar.getWidth();
            int leftWidth = (int) (totalWidth * leftPercent / 100.0);
            int rightWidth = (int) (totalWidth * rightPercent / 100.0);

            ViewGroup.LayoutParams leftParams = viewLeftProgress.getLayoutParams();
            leftParams.width = leftWidth;
            viewLeftProgress.setLayoutParams(leftParams);

            ViewGroup.LayoutParams rightParams = viewRightProgress.getLayoutParams();
            rightParams.width = rightWidth;
            viewRightProgress.setLayoutParams(rightParams);

            // 设置颜色
            viewLeftProgress.setBackgroundColor(barColor);
            // 辅助色稍浅
            viewRightProgress.setBackgroundColor(lightenColor(barColor, 0.3f));
        });
    }

    /**
     * 获取维度颜色
     */
    private int getDimensionColor(int index) {
        int[] colors = {
                Color.parseColor("#7C4DFF"), // E/I - 紫色
                Color.parseColor("#2196F3"), // S/N - 蓝色
                Color.parseColor("#F44336"), // T/F - 红色
                Color.parseColor("#4CAF50")  // J/P - 绿色
        };
        return colors[index % colors.length];
    }

    /**
     * 颜色变浅
     */
    private int lightenColor(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor)) + 255 * factor);
        int green = (int) ((Color.green(color) * (1 - factor)) + 255 * factor);
        int blue = (int) ((Color.blue(color) * (1 - factor)) + 255 * factor);
        return Color.rgb(red, green, blue);
    }

    /**
     * 设置 MBTI 类型图片
     */
    private void setMbtiTypeImage(String mbtiType) {
        if (mbtiType == null) {
            ivMbtiTypeImage.setImageResource(R.drawable.ic_psychology);
            return;
        }

        // 根据 MBTI 类型设置对应图片
        int imageRes = getMbtiImageResource(mbtiType);
        ivMbtiTypeImage.setImageResource(imageRes);
    }

    /**
     * 获取 MBTI 类型对应的图片资源
     */
    private int getMbtiImageResource(String mbtiType) {
        switch (mbtiType.toUpperCase()) {
            case "INTJ": return R.drawable.ic_mbti_intj;
            case "INTP": return R.drawable.ic_mbti_intp;
            case "ENTJ": return R.drawable.ic_mbti_entj;
            case "ENTP": return R.drawable.ic_mbti_entp;
            case "INFJ": return R.drawable.ic_mbti_infj;
            case "INFP": return R.drawable.ic_mbti_infp;
            case "ENFJ": return R.drawable.ic_mbti_enfj;
            case "ENFP": return R.drawable.ic_mbti_enfp;
            case "ISTJ": return R.drawable.ic_mbti_istj;
            case "ISFJ": return R.drawable.ic_mbti_isfj;
            case "ESTJ": return R.drawable.ic_mbti_estj;
            case "ESFJ": return R.drawable.ic_mbti_esfj;
            case "ISTP": return R.drawable.ic_mbti_istp;
            case "ISFP": return R.drawable.ic_mbti_isfp;
            case "ESTP": return R.drawable.ic_mbti_estp;
            case "ESFP": return R.drawable.ic_mbti_esfp;
            default: return R.drawable.ic_psychology;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
