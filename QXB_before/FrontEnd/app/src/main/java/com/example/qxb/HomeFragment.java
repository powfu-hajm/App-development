package com.example.qxb;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.example.qxb.model.PageResponse;
import com.example.qxb.models.User;
import com.example.qxb.models.network.ApiResponse;
import com.example.qxb.models.Article;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private int page = 1;
    private int size = 10;
    private CardView cardChat, cardDiary, cardTest;
    private RecyclerView recyclerViewArticles;
    private MultiTypeAdapter multiTypeAdapter;
    private List<ContentItem> contentList = new ArrayList<>();
    private ApiService apiService;

    // 时间显示相关
    private TextView tvDate, tvGreeting, tvCurrentTime;
    private Handler mainHandler;
    private ScheduledExecutorService timeScheduler;
    private boolean isTimeUpdating = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        apiService = RetrofitClient.getApiService();

        initViews(view);
        setupClickListeners();
        setupContentList();

        // 启动时间更新
        startRealTimeUpdates();

        // 更新问候语
        updateGreetingWithUserInfo();

        return view;
    }

    private void initViews(View view) {
        // 初始化功能卡片
        cardChat = view.findViewById(R.id.card_chat);
        cardDiary = view.findViewById(R.id.card_diary);
        cardTest = view.findViewById(R.id.card_test);
        recyclerViewArticles = view.findViewById(R.id.recyclerViewArticles);

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

        // 新增：当前时间显示控件（如果布局中没有，可以添加到布局中）
        // 注意：如果fragment_home.xml中没有tvCurrentTime，需要添加
        try {
            tvCurrentTime = view.findViewById(R.id.tvCurrentTime);
        } catch (Exception e) {
            tvCurrentTime = null;
        }
    }

    private void startRealTimeUpdates() {
        if (getActivity() == null) return;

        // 创建主线程Handler
        mainHandler = new Handler(Looper.getMainLooper());

        // 停止之前的更新
        stopRealTimeUpdates();

        // 创建定时调度器
        timeScheduler = Executors.newSingleThreadScheduledExecutor();
        isTimeUpdating = true;

        // 立即执行一次，然后每秒执行一次
        timeScheduler.scheduleAtFixedRate(() -> {
            if (!isTimeUpdating) return;

            updateCurrentTime();

            // 每分钟更新一次问候语（减少不必要的更新）
            Calendar calendar = Calendar.getInstance();
            if (calendar.get(Calendar.SECOND) == 0) {
                updateGreetingWithUserInfo();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void stopRealTimeUpdates() {
        isTimeUpdating = false;
        if (timeScheduler != null && !timeScheduler.isShutdown()) {
            timeScheduler.shutdownNow();
            timeScheduler = null;
        }
    }

    private void updateCurrentTime() {
        if (getActivity() == null) return;

        // 在主线程更新UI
        mainHandler.post(() -> {
            try {
                Calendar calendar = Calendar.getInstance();

                // 获取当前时间
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1; // 月份从0开始
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.SECOND);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                // 格式化星期
                String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
                String weekDay = weekDays[dayOfWeek - 1];

                // 格式化日期和时间
                String dateStr = String.format(Locale.CHINA, "%04d年%02d月%02d日 %s", year, month, day, weekDay);
                String timeStr = String.format(Locale.CHINA, "%02d:%02d:%02d", hour, minute, second);

                // 更新日期显示
                if (tvDate != null) {
                    tvDate.setText("今天是" + dateStr);
                }

                // 更新当前时间显示（精确到秒）
                if (tvCurrentTime != null) {
                    tvCurrentTime.setText("当前时间: " + timeStr);
                } else {
                    // 如果没有专门的当前时间控件，可以合并显示
                    if (tvDate != null) {
                        tvDate.setText("今天是" + dateStr + "  " + timeStr);
                    }
                }

                // 调试日志
                if (second == 0) { // 每分钟记录一次日志，避免过多日志
                    Log.d("HomeFragment", "时间更新: " + dateStr + " " + timeStr);
                }
            } catch (Exception e) {
                Log.e("HomeFragment", "更新日期时间失败: " + e.getMessage());
                // 使用简单的格式化作为后备
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 EEEE HH:mm:ss", Locale.CHINA);
                    String currentDateTime = sdf.format(new Date());
                    if (tvDate != null) {
                        tvDate.setText("今天是" + currentDateTime);
                    }
                } catch (Exception ex) {
                    Log.e("HomeFragment", "后备时间格式化失败: " + ex.getMessage());
                }
            }
        });
    }

    private String getTimeGreeting(int hour) {
        // 根据24小时制进行时间判断
        if (hour >= 0 && hour < 5) {
            return "夜深了";
        } else if (hour >= 5 && hour < 9) {
            return "早上好";
        } else if (hour >= 9 && hour < 12) {
            return "上午好";
        } else if (hour >= 12 && hour < 14) {
            return "中午好";
        } else if (hour >= 14 && hour < 18) {
            return "下午好";
        } else if (hour >= 18 && hour < 22) {
            return "晚上好";
        } else {
            return "晚上好";
        }
    }

    private void updateGreetingWithUserInfo() {
        if (tvGreeting == null || getActivity() == null) return;

        mainHandler.post(() -> {
            try {
                // 使用Calendar获取当前小时（24小时制）
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);

                String timeGreeting = getTimeGreeting(hour);
                Log.d("HomeFragment", "时间问候语: " + timeGreeting + " (当前小时: " + hour + ")");

                // 从MainActivity获取当前用户信息
                MainActivity mainActivity = (MainActivity) getActivity();
                String username = "用户";
                String nickname = null;
                boolean hasUser = false;

                if (mainActivity != null) {
                    User currentUser = mainActivity.getCurrentUser();
                    if (currentUser != null) {
                        // 优先使用昵称，如果没有昵称则使用用户名
                        nickname = currentUser.getNickname();
                        username = currentUser.getUsername() != null ? currentUser.getUsername() : "用户";
                        hasUser = true;
                        Log.d("HomeFragment", "获取到用户信息 - 昵称: " + nickname + ", 用户名: " + username);
                    }
                }

                String greeting;
                if (hasUser) {
                    if (nickname != null && !nickname.trim().isEmpty() && !nickname.equals("新用户")) {
                        greeting = timeGreeting + "，" + nickname;
                    } else {
                        greeting = timeGreeting + "，" + username;
                    }
                } else {
                    greeting = timeGreeting + "，用户";
                }

                Log.d("HomeFragment", "最终问候语: " + greeting);
                tvGreeting.setText(greeting);
            } catch (Exception e) {
                Log.e("HomeFragment", "更新问候语失败: " + e.getMessage());
                tvGreeting.setText("您好，欢迎回来");
            }
        });
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
        // 使用 getArticleList 方法，只需要 pageNum 和 pageSize 两个参数
        apiService.getArticleList(page, size)
                .enqueue(new Callback<PageResponse<Article>>() {
                    @Override
                    public void onResponse(Call<PageResponse<Article>> call,
                                           Response<PageResponse<Article>> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            PageResponse<Article> pageResponse = response.body();

                            // 使用 getData() 方法而不是 getRecords()
                            List<Article> articles = pageResponse.getData();

                            if (articles != null && !articles.isEmpty()) {
                                Log.d("HomeFragment", "文章列表大小: " + articles.size());
                                updateContentList(articles);
                            } else {
                                Log.d("HomeFragment", "文章列表为空，加载离线数据");
                                loadOfflineData();
                            }
                        } else {
                            Log.d("HomeFragment", "响应不成功或为空，加载离线数据");
                            loadOfflineData();
                        }
                    }

                    @Override
                    public void onFailure(Call<PageResponse<Article>> call, Throwable t) {
                        Log.e("HomeFragment", "请求失败: " + t.getMessage());
                        loadOfflineData();
                    }
                });
    }

    private void updateContentList(List<Article> articles) {
        if (getActivity() == null) return;

        mainHandler.post(() -> {
            contentList.clear();

            // 1. 心理日签
            ContentItem dailyQuote = new ContentItem(
                    "daily_quote",
                    "心理日签",
                    "什么是真正的爱？这是我见过最好的答案\n爱是接纳不完美，是给予自由，是在彼此陪伴中共同成长。",
                    "daily_quote"
            );
            dailyQuote.setCategory("每日一句");
            contentList.add(dailyQuote);

            // ⭐⭐ 2. 只保留前N条推文 ⭐⭐
            int MAX_SHOW = 5;  // 首页最多展示 5 篇
            List<Article> shortList = new ArrayList<>();

            if (articles != null && !articles.isEmpty()) {
                shortList = articles.size() > MAX_SHOW ? articles.subList(0, MAX_SHOW) : articles;
            }

            // 3. 添加推文到首页列表
            for (Article article : shortList) {
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

            // ⭐⭐4. 在底部添加 "查看更多" 按钮 ⭐⭐
            ContentItem moreItem = new ContentItem(
                    "more_articles",
                    "查看更多文章",
                    "前往文章列表",
                    "action_more"
            );
            contentList.add(moreItem);

            if (multiTypeAdapter != null) {
                multiTypeAdapter.notifyDataSetChanged();
            }
        });
    }

    private void loadOfflineData() {
        if (getActivity() == null) return;

        mainHandler.post(() -> {
            contentList.clear();

            // 1. 心理日签
            ContentItem dailyQuote = new ContentItem(
                    "daily_quote",
                    "心理日签",
                    "什么是真正的爱？这是我见过最好的答案\n爱是接纳不完美，是给予自由，是在彼此陪伴中共同成长。",
                    "daily_quote"
            );
            dailyQuote.setCategory("每日一句");
            contentList.add(dailyQuote);

            // 2. 离线模式下的文章
            ContentItem article1 = new ContentItem(
                    "1",
                    "工作多年突然觉得'没意思'？(离线模式)",
                    "探索职业倦怠背后的心理因素...",
                    "article"
            );
            article1.setReadTime("5分钟阅读");
            article1.setCategory("心理成长");
            contentList.add(article1);

            ContentItem article2 = new ContentItem(
                    "2",
                    "如何应对焦虑情绪？(离线模式)",
                    "焦虑是常见的情绪反应，但过度焦虑会影响生活...",
                    "article"
            );
            article2.setReadTime("3分钟阅读");
            article2.setCategory("情绪管理");
            contentList.add(article2);

            ContentItem article3 = new ContentItem(
                    "3",
                    "建立健康的人际边界(离线模式)",
                    "学会说'不'，是自我关爱的重要一步...",
                    "article"
            );
            article3.setReadTime("4分钟阅读");
            article3.setCategory("人际关系");
            contentList.add(article3);

            // 3. 查看更多按钮
            ContentItem moreItem = new ContentItem(
                    "more_articles",
                    "查看更多文章",
                    "前往文章列表",
                    "action_more"
            );
            contentList.add(moreItem);

            if (multiTypeAdapter != null) {
                multiTypeAdapter.notifyDataSetChanged();
            }
            Toast.makeText(getContext(), "网络不佳，已加载离线内容", Toast.LENGTH_SHORT).show();
        });
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

            case "action_more":
                Intent i = new Intent(getActivity(), ArticleListActivity.class);
                startActivity(i);
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
        // 确保时间更新在恢复时继续
        if (!isTimeUpdating) {
            startRealTimeUpdates();
        }

        // 刷新用户信息
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity.getCurrentUser() == null) {
                mainActivity.loadUserInfo();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 暂停时停止时间更新以节省资源
        stopRealTimeUpdates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 确保清理资源
        stopRealTimeUpdates();
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
    }
}