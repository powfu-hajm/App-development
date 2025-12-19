package com.example.qxb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ArticleDetailActivity extends AppCompatActivity {

    private TextView tvArticleTitle, tvArticleContent, tvReadTime, tvCategory, tvPublishDate;
    private ImageView ivLike, ivFavorite;
    private TextView tvLikeCount;
    private WebView webView;
    private ScrollView scrollViewContent;

    private boolean isLiked = false;
    private boolean isFavorited = false;
    private int likeCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        initViews();
        setupToolbar();
        setupWebView();
        loadArticleData();
        setupClickListeners();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        tvArticleTitle = findViewById(R.id.tvArticleTitle);
        tvArticleContent = findViewById(R.id.tvArticleContent);
        tvReadTime = findViewById(R.id.tvReadTime);
        tvCategory = findViewById(R.id.tvCategory);
        tvPublishDate = findViewById(R.id.tvPublishDate);
        ivLike = findViewById(R.id.ivLike);
        ivFavorite = findViewById(R.id.ivFavorite);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        
        webView = findViewById(R.id.webView);
        scrollViewContent = findViewById(R.id.scrollViewContent);
    }

    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setBlockNetworkImage(false); // 允许加载图片
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
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

    private void loadArticleData() {
        Intent intent = getIntent();
        if (intent == null) {
            showSampleContent();
            return;
        }

        String articleTitle = intent.getStringExtra("article_title");
        String articleUrl = intent.getStringExtra("article_url"); // 获取URL
        String articleContent = intent.getStringExtra("article_content");
        String readTime = intent.getStringExtra("read_time");
        String category = intent.getStringExtra("category");

        // 设置标题等基础信息
        tvArticleTitle.setText(articleTitle != null ? articleTitle : "文章标题");
        tvReadTime.setText(readTime != null ? readTime : "5分钟阅读");
        tvCategory.setText(category != null ? category : "心理成长");
        tvPublishDate.setText("2024-01-15");

        // 优先显示 URL
        if (articleUrl != null && !articleUrl.isEmpty()) {
            webView.setVisibility(View.VISIBLE);
            scrollViewContent.setVisibility(View.GONE);
            webView.loadUrl(articleUrl);
        } else if (articleContent != null) {
            tvArticleContent.setText(articleContent);
            scrollViewContent.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
        } else {
            showSampleContent();
        }
    }

    private void showSampleContent() {
        // 显示示例内容
        tvArticleTitle.setText("工作多年突然觉得'没意思'？或许你该寻找自己的人生主线了");
        tvArticleContent.setText("示例内容...");
        scrollViewContent.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
    }

    private void setupClickListeners() {
        findViewById(R.id.layoutLike).setOnClickListener(v -> toggleLike());
        findViewById(R.id.layoutFavorite).setOnClickListener(v -> toggleFavorite());
        findViewById(R.id.layoutShare).setOnClickListener(v -> shareArticle());
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
