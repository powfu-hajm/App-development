package com.example.qxb;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qxb.models.network.ApiResponse;
import com.example.qxb.widgets.EmptyStateView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private CardView cardChat, cardDiary, cardTest;
    private RecyclerView recyclerViewArticles;
    private EmptyStateView emptyStateView;
    private MultiTypeAdapter multiTypeAdapter;
    private List<ContentItem> contentList = new ArrayList<>();
    private ApiService apiService;

    // 时间显示相关
    private TextView tvDate, tvGreeting;
    private Handler timeHandler;
    private Runnable timeRunnable;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        apiService = RetrofitClient.getApiService();

        initViews(view);
        setupClickListeners();
        setupContentList();
        startTimeUpdates();

        return view;
    }

    private void initViews(View view) {
        // 初始化功能卡片
        cardChat = view.findViewById(R.id.card_chat);
        cardDiary = view.findViewById(R.id.card_diary);
        cardTest = view.findViewById(R.id.card_test);
        recyclerViewArticles = view.findViewById(R.id.recyclerViewArticles);
        emptyStateView = view.findViewById(R.id.emptyStateView);

        // 安全初始化时间显示视图
        try {
            tvDate = view.findViewById(R.id.tvDate);
        } catch (Exception e) {
            tvDate = null;
        }

        try {
            tvGreeting = view.findViewById(R.id.tvGreeting);
        } catch (Exception e) {
            tvGreeting = null;
        }
    }

    private void startTimeUpdates() {
        if (tvDate == null && tvGreeting == null) {
            return;
        }

        timeHandler = new Handler();
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                updateCurrentTime();
                updateGreeting();
                timeHandler.postDelayed(this, 60000);
            }
        };

        updateCurrentTime();
        updateGreeting();
        timeHandler.postDelayed(timeRunnable, 60000);
    }

    private void updateCurrentTime() {
        if (tvDate == null) return;

        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 EEEE", Locale.CHINA);
                String currentDate = dateFormat.format(new Date());
                tvDate.setText("今天是" + currentDate);
            });
        }
    }

    private void updateGreeting() {
        if (tvGreeting == null) return;

        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH", Locale.CHINA);
                int hour = Integer.parseInt(timeFormat.format(new Date()));

                String greeting;
                if (hour >= 5 && hour < 12) {
                    greeting = "早上好，用户";
                } else if (hour >= 12 && hour < 18) {
                    greeting = "下午好，用户";
                } else {
                    greeting = "晚上好，用户";
                }

                tvGreeting.setText(greeting);
            });
        }
    }

    private void setupContentList() {
        if (recyclerViewArticles != null) {
            contentList.clear();
            recyclerViewArticles.setLayoutManager(new LinearLayoutManager(getContext()));
            multiTypeAdapter = new MultiTypeAdapter(contentList, this::handleContentClick);
            recyclerViewArticles.setAdapter(multiTypeAdapter);
            
            // 从网络加载数据
            loadArticlesFromNetwork();
        }
    }

    /**
     * 从后端API加载文章列表
     */
    private void loadArticlesFromNetwork() {
        if (apiService == null) return;

        Log.d("HomeFragment", "开始加载文章列表...");
        apiService.getArticles().enqueue(new Callback<ApiResponse<List<Article>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Article>>> call, Response<ApiResponse<List<Article>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Article>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        List<Article> articles = apiResponse.getData();
                        Log.e("HomeFragment", "成功获取文章，数量: " + (articles != null ? articles.size() : 0));
                        
                        updateContentList(articles);
                    } else {
                        Log.e("HomeFragment", "获取文章失败: " + apiResponse.getMessage());
                        loadOfflineData();
                    }
                } else {
                    Log.e("HomeFragment", "网络请求失败: " + response.code());
                    loadOfflineData();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Article>>> call, Throwable t) {
                Log.e("HomeFragment", "网络连接错误", t);
                loadOfflineData();
            }
        });
    }

    private void updateContentList(List<Article> articles) {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            // 隐藏空状态
            hideEmptyState();

            contentList.clear();
            
            // 1. 添加一个固定的心理日签
            ContentItem dailyQuote = new ContentItem(
                    "daily_quote",
                    "心理日签",
                    "什么是真正的爱？这是我见过最好的答案\n爱是接纳不完美，是给予自由，是在彼此陪伴中共同成长。",
                    "daily_quote"
            );
            dailyQuote.setCategory("每日一句");
            contentList.add(dailyQuote);

            // 2. 添加爬取到的文章
            for (Article article : articles) {
                ContentItem item = new ContentItem(
                        String.valueOf(article.getId()),
                        article.getTitle(),
                        article.getSummary(),
                        "article"
                );
                item.setReadTime(article.getReadCount() + "阅读");
                item.setCategory(article.getSource());
                item.setMediaUrl(article.getOriginalUrl());
                
                contentList.add(item);
            }

            // 3. 通知适配器更新
            if (multiTypeAdapter != null) {
                multiTypeAdapter.notifyDataSetChanged();
            }
        });
    }

    private void loadOfflineData() {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            // 显示网络错误的空状态
            showNetworkError();
        });
    }

    /**
     * 显示网络错误空状态
     */
    private void showNetworkError() {
        if (emptyStateView != null && recyclerViewArticles != null) {
            recyclerViewArticles.setVisibility(View.GONE);
            emptyStateView.setVisibility(View.VISIBLE);
            emptyStateView.setEmptyType(EmptyStateView.EmptyType.NETWORK_ERROR);
            emptyStateView.setAction("重新加载", v -> loadArticlesFromNetwork());
        }
    }

    /**
     * 显示文章为空的状态
     */
    private void showEmptyArticles() {
        if (emptyStateView != null && recyclerViewArticles != null) {
            recyclerViewArticles.setVisibility(View.GONE);
            emptyStateView.setVisibility(View.VISIBLE);
            emptyStateView.setEmptyType(EmptyStateView.EmptyType.NO_ARTICLE);
            emptyStateView.setAction("刷新", v -> loadArticlesFromNetwork());
        }
    }

    /**
     * 隐藏空状态，显示列表
     */
    private void hideEmptyState() {
        if (emptyStateView != null && recyclerViewArticles != null) {
            emptyStateView.setVisibility(View.GONE);
            recyclerViewArticles.setVisibility(View.VISIBLE);
        }
    }

    private void setupClickListeners() {
        if (cardChat != null) {
            cardChat.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).switchToChat();
                }
            });
        }

        if (cardDiary != null) {
            cardDiary.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), DiaryListActivity.class);
                startActivity(intent);
            });
        }

        if (cardTest != null) {
            cardTest.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), TestActivity.class);
                startActivity(intent);
            });
        }
    }

    private void handleContentClick(ContentItem item) {
        if (item == null) return;

        Intent intent = null;

        switch (item.getType()) {
            case "daily_quote":
                intent = new Intent(getActivity(), ArticleDetailActivity.class);
                intent.putExtra("article_title", item.getTitle());
                intent.putExtra("article_content", item.getDescription());
                intent.putExtra("read_time", "1分钟阅读");
                intent.putExtra("category", "每日一句");
                break;

            case "article":
            default:
                intent = new Intent(getActivity(), ArticleDetailActivity.class);
                intent.putExtra("article_title", item.getTitle());
                if (item.getMediaUrl() != null && item.getMediaUrl().startsWith("http")) {
                    intent.putExtra("article_url", item.getMediaUrl());
                }
                intent.putExtra("article_content", item.getDescription());
                intent.putExtra("read_time", item.getReadTime());
                intent.putExtra("category", item.getCategory());
                break;
        }

        if (intent != null) {
            intent.putExtra("content_id", item.getId());
            intent.putExtra("content_type", item.getType());
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCurrentTime();
        updateGreeting();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (timeHandler != null && timeRunnable != null) {
            timeHandler.removeCallbacks(timeRunnable);
        }
    }
}
