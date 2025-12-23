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

import com.example.qxb.models.PageResponse;
import com.example.qxb.models.User;
import com.example.qxb.models.Article;
import com.example.qxb.models.network.ApiResponse;

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
    private int size = 6;
    private CardView cardChat, cardDiary, cardTest;
    private RecyclerView recyclerViewArticles;
    private TextView tvEmpty;

    // 关键修改：这里使用final确保列表引用不变
    private final List<Article> contentList = new ArrayList<>();
    private MultiTypeAdapter multiTypeAdapter;
    private ApiService apiService;

    // 时间显示相关
    private TextView tvDate, tvGreeting, tvCurrentTime;
    private Handler mainHandler;
    private ScheduledExecutorService timeScheduler;
    private boolean isTimeUpdating = false;

    // 修改类型ID数组，先尝试typeId=1（专栏），这是后端实际有的类型
    private Integer[] typeIds = {1, 4, 5, 3, 2}; // 专栏, 自我成长, 情感关系, 科普, 测试
    private int currentTypeIndex = 0;

    // 添加加载状态标志
    private boolean isLoading = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 初始化Retrofit
        if (RetrofitClient.getApiService() == null && getActivity() != null) {
            RetrofitClient.init(getActivity().getApplicationContext());
        }
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
        tvEmpty = view.findViewById(R.id.tvEmpty);

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

        timeScheduler.scheduleAtFixedRate(() -> {
            if (!isTimeUpdating) return;

            updateCurrentTime();

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

        mainHandler.post(() -> {
            try {
                Calendar calendar = Calendar.getInstance();

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.SECOND);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
                String weekDay = weekDays[dayOfWeek - 1];

                String dateStr = String.format(Locale.CHINA, "%04d年%02d月%02d日 %s", year, month, day, weekDay);
                String timeStr = String.format(Locale.CHINA, "%02d:%02d:%02d", hour, minute, second);

                if (tvDate != null) {
                    tvDate.setText("今天是" + dateStr);
                }

                if (tvCurrentTime != null) {
                    tvCurrentTime.setText("当前时间: " + timeStr);
                } else {
                    if (tvDate != null) {
                        tvDate.setText("今天是" + dateStr + "  " + timeStr);
                    }
                }

                if (second == 0) {
                    Log.d("HomeFragment", "时间更新: " + dateStr + " " + timeStr);
                }
            } catch (Exception e) {
                Log.e("HomeFragment", "更新日期时间失败: " + e.getMessage());
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
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);

                String timeGreeting = getTimeGreeting(hour);

                MainActivity mainActivity = (MainActivity) getActivity();
                String username = "用户";
                String nickname = null;
                boolean hasUser = false;

                if (mainActivity != null) {
                    User currentUser = mainActivity.getCurrentUser();
                    if (currentUser != null) {
                        nickname = currentUser.getNickname();
                        username = currentUser.getUsername() != null ? currentUser.getUsername() : "用户";
                        hasUser = true;
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

                tvGreeting.setText(greeting);
            } catch (Exception e) {
                Log.e("HomeFragment", "更新问候语失败: " + e.getMessage());
                tvGreeting.setText("您好，欢迎回来");
            }
        });
    }

    private void setupContentList() {
        if (recyclerViewArticles != null && getActivity() != null) {
            // 显示加载中
            if (tvEmpty != null) {
                tvEmpty.setText("正在加载文章...");
                tvEmpty.setVisibility(View.VISIBLE);
            }

            // 设置布局管理器
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerViewArticles.setLayoutManager(layoutManager);

            // 创建适配器 - 使用同一个contentList对象
            multiTypeAdapter = new MultiTypeAdapter(contentList, new MultiTypeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Article item) {
                    handleArticleClick(item);
                }
            });

            recyclerViewArticles.setAdapter(multiTypeAdapter);
            Log.d("HomeFragment", "setupContentList完成，开始加载文章");

            // 加载网络文章
            loadArticlesFromNetwork();
        } else {
            Log.e("HomeFragment", "recyclerViewArticles为null或Activity为null");
        }
    }

    private void handleArticleClick(Article article) {
        if (article == null || getActivity() == null) return;

        // 检查是否是特殊功能项
        if ("daily_quote".equals(article.getOriginalUrl())) {
            // 心理日签点击处理
            Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
            intent.putExtra("article_title", article.getTitle());
            intent.putExtra("article_content", article.getSummary());
            intent.putExtra("read_time", "1分钟阅读");
            intent.putExtra("category", article.getSource());
            intent.putExtra("content_id", article.getId() != null ? article.getId() : 0L);
            startActivity(intent);
        } else if ("action_more".equals(article.getSource())) {
            // 查看更多文章
            Intent intent = new Intent(getActivity(), ArticleListActivity.class);
            startActivity(intent);
        } else if (article.getId() != null && article.getId().intValue() > 0) {
            // 普通文章
            Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
            intent.putExtra("article_title", article.getTitle());
            intent.putExtra("article_content", article.getSummary());
            intent.putExtra("read_time", (article.getReadCount() != null ? article.getReadCount() : 0) + "阅读");
            intent.putExtra("category", article.getSource() != null ? article.getSource() : "心理成长");
            intent.putExtra("content_id", article.getId());

            // 添加文章URL用于WebView显示
            if (article.getOriginalUrl() != null && article.getOriginalUrl().startsWith("http")) {
                intent.putExtra("article_url", article.getOriginalUrl());
            }

            startActivity(intent);
        } else {
            // 其他情况
            Toast.makeText(getActivity(), "正在加载，请稍候", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 从后端API加载文章列表
     */
    private void loadArticlesFromNetwork() {
        if (isLoading) {
            Log.d("HomeFragment", "正在加载中，跳过");
            return;
        }

        if (apiService == null) {
            Log.e("HomeFragment", "ApiService为空，无法加载文章");
            showOfflineData();
            return;
        }

        isLoading = true;

        // 获取当前类型ID
        Integer typeId = typeIds[currentTypeIndex];
        Log.d("HomeFragment", "加载文章，类型ID: " + typeId + ", 页码: " + page + ", 大小: " + size);

        Call<ApiResponse<PageResponse<Article>>> call = apiService.getArticlePage(page, size, typeId);
        call.enqueue(new Callback<ApiResponse<PageResponse<Article>>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<PageResponse<Article>>> call,
                    Response<ApiResponse<PageResponse<Article>>> response) {

                isLoading = false;
                Log.d("HomeFragment", "网络响应: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PageResponse<Article>> resp = response.body();
                    Log.d("HomeFragment", "API响应: success=" + resp.isSuccess() + ", message=" + resp.getMessage());

                    if (resp.isSuccess() && resp.getData() != null) {
                        PageResponse<Article> pageData = resp.getData();
                        List<Article> articles = pageData.getRecords();

                        Log.d("HomeFragment", "获取到文章数据: total=" + pageData.getTotal() + ", records=" + (articles != null ? articles.size() : 0));

                        if (articles != null && !articles.isEmpty()) {
                            Log.d("HomeFragment", "成功加载 " + articles.size() + " 篇文章");

                            // 更新显示列表
                            updateContentListWithArticles(articles);
                        } else {
                            Log.w("HomeFragment", "文章列表为空，尝试下一个类型");
                            // 尝试下一个类型
                            loadNextTypeArticles();
                        }
                    } else {
                        Log.e("HomeFragment", "API响应失败: " + resp.getMessage());
                        showOfflineData();
                    }
                } else {
                    Log.e("HomeFragment", "HTTP请求失败: " + response.code() + ", message: " + response.message());
                    showOfflineData();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<Article>>> call, Throwable t) {
                isLoading = false;
                Log.e("HomeFragment", "网络请求失败: " + t.getMessage());
                showOfflineData();
            }
        });
    }

    /**
     * 尝试加载下一个类型的文章
     */
    private void loadNextTypeArticles() {
        currentTypeIndex++;
        if (currentTypeIndex < typeIds.length) {
            Log.d("HomeFragment", "尝试下一个文章类型，索引: " + currentTypeIndex + ", typeId=" + typeIds[currentTypeIndex]);
            loadArticlesFromNetwork();
        } else {
            Log.w("HomeFragment", "所有类型文章都为空，显示离线数据");
            showOfflineData();
        }
    }

    /**
     * 使用实际文章更新内容显示列表
     */
    private void updateContentListWithArticles(List<Article> articles) {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            // 清除旧数据
            contentList.clear();
            Log.d("HomeFragment", "清空contentList，当前大小: " + contentList.size());

            // 隐藏空状态提示
            if (tvEmpty != null) {
                tvEmpty.setVisibility(View.GONE);
            }

            // 1. 添加心理日签
            Article dailyQuote = new Article();
            dailyQuote.setId(9999L); // 特殊ID用于标识心理日签
            dailyQuote.setTitle("心理日签");
            dailyQuote.setSummary("什么是真正的爱？这是我见过最好的答案\n爱是接纳不完美，是给予自由，是在彼此陪伴中共同成长。");
            dailyQuote.setReadCount(0);
            dailyQuote.setSource("每日一句");
            dailyQuote.setOriginalUrl("daily_quote");
            contentList.add(dailyQuote);
            Log.d("HomeFragment", "添加心理日签，当前大小: " + contentList.size());

            // 2. 添加实际文章（限制最多5条）
            int MAX_SHOW = Math.min(5, articles.size());
            Log.d("HomeFragment", "显示 " + MAX_SHOW + " 篇文章");

            for (int i = 0; i < MAX_SHOW; i++) {
                Article article = articles.get(i);
                // 确保文章有标题
                if (article.getTitle() != null && !article.getTitle().isEmpty()) {
                    // 确保source字段不为空
                    if (article.getSource() == null || article.getSource().isEmpty()) {
                        article.setSource(article.getType() != null ? article.getType() : "心理文章");
                    }

                    contentList.add(article);
                    Log.d("HomeFragment", "添加文章到显示列表: " + article.getTitle() + ", source=" + article.getSource() + ", 当前大小: " + contentList.size());
                }
            }

            // 3. 添加查看更多按钮
            Article moreItem = new Article();
            moreItem.setId(9998L); // 特殊ID用于标识查看更多
            moreItem.setTitle("查看更多文章");
            moreItem.setSummary("探索更多心理文章和资源");
            moreItem.setSource("action_more");
            contentList.add(moreItem);
            Log.d("HomeFragment", "添加查看更多按钮，当前大小: " + contentList.size());

            // 重要：直接通知适配器数据已变化，不要调用updateData方法
            if (multiTypeAdapter != null) {
                // 不再调用updateData，直接通知适配器刷新
                multiTypeAdapter.notifyDataSetChanged();
                Log.d("HomeFragment", "适配器通知刷新，共 " + contentList.size() + " 项");

                // 检查RecyclerView是否可见
                if (recyclerViewArticles.getVisibility() != View.VISIBLE) {
                    recyclerViewArticles.setVisibility(View.VISIBLE);
                    Log.d("HomeFragment", "设置RecyclerView可见");
                }

                // 显示成功提示
                if (MAX_SHOW > 0) {
                    Toast.makeText(getActivity(), "已加载" + MAX_SHOW + "篇文章", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("HomeFragment", "multiTypeAdapter为null，无法更新UI");
                showEmptyState();
            }

            // 增加页码以便下次加载
            page++;
        });
    }

    private void showEmptyState() {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            if (tvEmpty != null) {
                tvEmpty.setText("暂无文章，下拉刷新");
                tvEmpty.setVisibility(View.VISIBLE);
            }
            if (recyclerViewArticles != null) {
                recyclerViewArticles.setVisibility(View.GONE);
            }
        });
    }

    private void loadOfflineData() {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            contentList.clear();
            Log.d("HomeFragment", "加载离线数据，清空contentList");

            // 隐藏空状态提示
            if (tvEmpty != null) {
                tvEmpty.setVisibility(View.GONE);
            }

            // 1. 心理日签
            Article dailyQuote = new Article();
            dailyQuote.setId(9999L);
            dailyQuote.setTitle("心理日签");
            dailyQuote.setSummary("什么是真正的爱？这是我见过最好的答案\n爱是接纳不完美，是给予自由，是在彼此陪伴中共同成长。");
            dailyQuote.setReadCount(0);
            dailyQuote.setSource("每日一句");
            dailyQuote.setOriginalUrl("daily_quote");
            contentList.add(dailyQuote);

            // 2. 离线模式下的文章
            Article article1 = new Article();
            article1.setId(1L);
            article1.setTitle("工作多年突然觉得'没意思'？(离线模式)");
            article1.setSummary("探索职业倦怠背后的心理因素...");
            article1.setReadCount(5);
            article1.setSource("心理成长");
            article1.setType("专栏");
            contentList.add(article1);

            Article article2 = new Article();
            article2.setId(2L);
            article2.setTitle("如何应对焦虑情绪？(离线模式)");
            article2.setSummary("焦虑是常见的情绪反应，但过度焦虑会影响生活...");
            article2.setReadCount(3);
            article2.setSource("情绪管理");
            article2.setType("科普");
            contentList.add(article2);

            Article article3 = new Article();
            article3.setId(3L);
            article3.setTitle("建立健康的人际边界(离线模式)");
            article3.setSummary("学会说'不'，是自我关爱的重要一步...");
            article3.setReadCount(4);
            article3.setSource("人际关系");
            article3.setType("自我成长");
            contentList.add(article3);

            // 3. 查看更多按钮
            Article moreItem = new Article();
            moreItem.setId(9998L);
            moreItem.setTitle("查看更多文章");
            moreItem.setSummary("前往文章列表");
            moreItem.setSource("action_more");
            contentList.add(moreItem);

            if (multiTypeAdapter != null) {
                multiTypeAdapter.notifyDataSetChanged();
                if (recyclerViewArticles != null) {
                    recyclerViewArticles.setVisibility(View.VISIBLE);
                }
                Log.d("HomeFragment", "离线数据加载完成，共 " + contentList.size() + " 项");
            } else {
                showEmptyState();
            }
            Toast.makeText(getActivity(), "网络不佳，已加载离线内容", Toast.LENGTH_SHORT).show();
        });
    }

    private void showOfflineData() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getActivity(), "网络异常，加载本地缓存", Toast.LENGTH_SHORT).show();
                loadOfflineData();
            });
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

    @Override
    public void onResume() {
        super.onResume();
        if (!isTimeUpdating) {
            startRealTimeUpdates();
        }

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity.getCurrentUser() == null) {
                mainActivity.loadUserInfo();
            }
        }

        // 刷新文章列表 - 只有当没有文章数据时才刷新
        if (contentList.isEmpty() || contentList.size() <= 2) { // 只有日签和查看更多按钮，或者空
            Log.d("HomeFragment", "onResume: 重新加载文章");
            page = 1;
            currentTypeIndex = 0;
            loadArticlesFromNetwork();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRealTimeUpdates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRealTimeUpdates();
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
    }
}