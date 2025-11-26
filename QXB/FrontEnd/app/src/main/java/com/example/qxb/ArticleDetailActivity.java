package com.example.qxb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.qxb.R;

public class ArticleDetailActivity extends AppCompatActivity {

    private TextView tvArticleTitle, tvArticleContent, tvReadTime, tvCategory, tvPublishDate;
    private ImageView ivLike, ivFavorite;
    private TextView tvLikeCount;

    private boolean isLiked = false;
    private boolean isFavorited = false;
    private int likeCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        initViews();
        setupToolbar();
        loadArticleData();
        setupClickListeners();
    }

    private void initViews() {
        // 初始化Toolbar - 现在布局中有这个ID了
        Toolbar toolbar = findViewById(R.id.toolbar);

        // 初始化其他视图
        tvArticleTitle = findViewById(R.id.tvArticleTitle);
        tvArticleContent = findViewById(R.id.tvArticleContent);
        tvReadTime = findViewById(R.id.tvReadTime);
        tvCategory = findViewById(R.id.tvCategory);
        tvPublishDate = findViewById(R.id.tvPublishDate);
        ivLike = findViewById(R.id.ivLike);
        ivFavorite = findViewById(R.id.ivFavorite);
        tvLikeCount = findViewById(R.id.tvLikeCount);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 设置返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 设置返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadArticleData() {
        // 安全地获取Intent数据
        Intent intent = getIntent();
        if (intent == null) {
            // 如果没有数据，显示示例内容
            showSampleContent();
            return;
        }

        String articleTitle = intent.getStringExtra("article_title");
        String articleContent = intent.getStringExtra("article_content");
        String readTime = intent.getStringExtra("read_time");
        String category = intent.getStringExtra("category");

        // 设置文章数据
        if (articleTitle != null) {
            tvArticleTitle.setText(articleTitle);
        } else {
            tvArticleTitle.setText("文章标题");
        }

        if (articleContent != null) {
            tvArticleContent.setText(articleContent);
        } else {
            tvArticleContent.setText(getSampleArticleContent());
        }

        if (readTime != null) {
            tvReadTime.setText(readTime);
        } else {
            tvReadTime.setText("5分钟阅读");
        }

        if (category != null) {
            tvCategory.setText(category);
        } else {
            tvCategory.setText("心理成长");
        }

        // 设置发布日期
        tvPublishDate.setText("2024-01-15");
    }

    private void showSampleContent() {
        // 显示示例内容
        tvArticleTitle.setText("工作多年突然觉得'没意思'？或许你该寻找自己的人生主线了");
        tvArticleContent.setText(getSampleArticleContent());
        tvReadTime.setText("5分钟阅读");
        tvCategory.setText("心理成长");
        tvPublishDate.setText("2024-01-15");
    }

    private void setupClickListeners() {
        // 点赞功能
        findViewById(R.id.layoutLike).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLike();
            }
        });

        // 收藏功能
        findViewById(R.id.layoutFavorite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite();
            }
        });

        // 分享功能
        findViewById(R.id.layoutShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareArticle();
            }
        });
    }

    private void toggleLike() {
        isLiked = !isLiked;
        if (isLiked) {
            likeCount++;
            ivLike.setImageResource(android.R.drawable.btn_star_big_on);
            ivLike.setColorFilter(getResources().getColor(android.R.color.holo_red_light));
        } else {
            likeCount--;
            ivLike.setImageResource(android.R.drawable.btn_star_big_off);
            ivLike.setColorFilter(getResources().getColor(android.R.color.darker_gray));
        }
        tvLikeCount.setText(likeCount + " 点赞");
    }

    private void toggleFavorite() {
        isFavorited = !isFavorited;
        if (isFavorited) {
            ivFavorite.setImageResource(android.R.drawable.star_on);
            ivFavorite.setColorFilter(getResources().getColor(android.R.color.holo_orange_light));
        } else {
            ivFavorite.setImageResource(android.R.drawable.star_off);
            ivFavorite.setColorFilter(getResources().getColor(android.R.color.darker_gray));
        }
    }

    private void shareArticle() {
        String shareText = tvArticleTitle.getText() + " - 来自心理助手App";
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "分享文章"));
    }

    /**
     * 获取示例文章内容
     */
    private String getSampleArticleContent() {
        return "工作多年后，很多人会经历所谓的'职业倦怠期'。这种感受并非个例，而是现代职场中普遍存在的现象。\n\n" +
                "## 职业倦怠的表现\n\n" +
                "1. **情感枯竭**：对工作失去热情，感到精疲力尽\n" +
                "2. **去个性化**：对同事和客户变得冷漠、疏远\n" +
                "3. **个人成就感降低**：怀疑自己的工作价值和能力\n\n" +
                "## 重新发现工作意义的方法\n\n" +
                "### 1. 反思核心价值观\n" +
                "问自己：什么对我真正重要？我的工作如何与个人价值观对齐？\n\n" +
                "### 2. 设定新的挑战\n" +
                "寻找工作中的学习机会，设定短期可实现的目标。\n\n" +
                "### 3. 建立支持网络\n" +
                "与志同道合的同事建立联系，分享经验和感受。\n\n" +
                "### 4. 工作与生活平衡\n" +
                "确保有足够的休息和娱乐时间，培养工作外的兴趣爱好。\n\n" +
                "## 结语\n\n" +
                "职业倦怠不是终点，而是重新评估和调整职业道路的契机。通过深入理解自己的需求和价值观，你可以找到新的人生主线，让工作重新变得有意义。";
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}