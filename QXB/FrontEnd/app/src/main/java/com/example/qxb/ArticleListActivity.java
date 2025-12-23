package com.example.qxb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qxb.models.Article;
import com.example.qxb.models.network.ApiResponse;
import com.example.qxb.models.PageResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MultiTypeAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private Toolbar toolbar; // 添加Toolbar引用

    private Integer typeId;

    // ⭐统一存放 Article，不再用 ContentItem
    private final List<Article> list = new ArrayList<>();

    private int pageNum = 1;   // 当前页码
    private final int pageSize = 10;
    private boolean isLoading = false;
    private boolean hasNextPage = true;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        // 初始化Toolbar
        setupToolbar();

        // 初始化Retrofit
        if (RetrofitClient.getApiService() == null) {
            RetrofitClient.init(getApplicationContext());
        }
        apiService = RetrofitClient.getApiService();

        initView();
        initListeners();
        loadData(true);
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.titleBar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);

            // 显示返回按钮
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }

            // 设置返回按钮点击事件
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed(); // 调用系统返回
                }
            });
        }
    }

    private void showOfflineData() {
        runOnUiThread(() -> {
            Toast.makeText(this, "加载失败", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            isLoading = false;

            // 显示离线数据
            if (list.isEmpty()) {
                list.clear();

                // 添加离线文章
                Article article1 = new Article();
                article1.setId(1L);
                article1.setTitle("离线文章示例1");
                article1.setSummary("这是离线模式下的文章内容");
                article1.setReadCount(10);
                article1.setSource("心理成长");
                article1.setType("专栏");
                list.add(article1);

                Article article2 = new Article();
                article2.setId(2L);
                article2.setTitle("离线文章示例2");
                article2.setSummary("请检查网络连接后重试");
                article2.setReadCount(5);
                article2.setSource("情绪管理");
                article2.setType("科普");
                list.add(article2);

                adapter.notifyDataSetChanged();
            }
        });
    }


    private void initView() {
        recyclerView = findViewById(R.id.articleRecyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        progressBar = findViewById(R.id.progressBar);

        adapter = new MultiTypeAdapter(
                list,
                item -> {
                    if (item.getId() != null && item.getId().intValue() > 0) {
                        // 跳转到文章详情页
                        Intent intent = new Intent(ArticleListActivity.this, ArticleDetailActivity.class);
                        intent.putExtra("article_title", item.getTitle());
                        intent.putExtra("article_content", item.getSummary());
                        intent.putExtra("read_time", (item.getReadCount() != null ? item.getReadCount() : 0) + "阅读");
                        intent.putExtra("category", item.getSource() != null ? item.getSource() : "心理成长");
                        intent.putExtra("content_id", item.getId());

                        if (item.getOriginalUrl() != null && item.getOriginalUrl().startsWith("http")) {
                            intent.putExtra("article_url", item.getOriginalUrl());
                        }

                        startActivity(intent);
                    } else {
                        Toast.makeText(ArticleListActivity.this, "点击：" + item.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    private void initListeners() {

        // 下拉刷新
        swipeRefreshLayout.setOnRefreshListener(() -> {
            pageNum = 1;
            hasNextPage = true;
            list.clear();
            adapter.notifyDataSetChanged();
            loadData(false);
        });

        // RecyclerView 滚动到底自动加载
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);

                if (!isLoading && hasNextPage) {
                    LinearLayoutManager manager = (LinearLayoutManager) rv.getLayoutManager();

                    if (manager != null &&
                            manager.findLastVisibleItemPosition() >= list.size() - 3) {
                        loadData(false);
                    }
                }
            }
        });
    }

    private void loadData(boolean firstLoad) {
        if (isLoading) return;
        isLoading = true;

        if (firstLoad) {
            progressBar.setVisibility(View.VISIBLE);
        }

        if (apiService == null) {
            Log.e("ArticleListActivity", "ApiService为空");
            showOfflineData();
            return;
        }

        Call<ApiResponse<PageResponse<Article>>> call =
                apiService.getArticlePage(pageNum, pageSize, typeId);

        call.enqueue(new Callback<ApiResponse<PageResponse<Article>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<Article>>> call,
                                   Response<ApiResponse<PageResponse<Article>>> response) {

                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);

                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("ArticleListActivity", "响应失败: " + response.code());
                    if (list.isEmpty()) {
                        showOfflineData();
                    }
                    return;
                }

                ApiResponse<PageResponse<Article>> apiResponse = response.body();

                if (!apiResponse.isSuccess() || apiResponse.getData() == null) {
                    Log.e("ArticleListActivity", "API响应失败: " + apiResponse.getMessage());
                    if (list.isEmpty()) {
                        showOfflineData();
                    }
                    return;
                }

                PageResponse<Article> pageData = apiResponse.getData();
                List<Article> articles = pageData.getRecords();

                if (articles != null && !articles.isEmpty()) {
                    // 添加新数据
                    int startPosition = list.size();
                    list.addAll(articles);

                    // 通知适配器有新增数据
                    if (startPosition == 0) {
                        adapter.notifyDataSetChanged();
                    } else {
                        adapter.notifyItemRangeInserted(startPosition, articles.size());
                    }

                    pageNum++;

                    // 检查是否还有更多数据
                    if (articles.size() < pageSize) {
                        hasNextPage = false;
                    }
                } else {
                    hasNextPage = false;
                    if (list.isEmpty()) {
                        showOfflineData();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<Article>>> call, Throwable t) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);

                Log.e("ArticleListActivity", "网络请求失败: " + t.getMessage());
                if (list.isEmpty()) {
                    showOfflineData();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 刷新页面时重新加载数据
        if (list.isEmpty()) {
            pageNum = 1;
            hasNextPage = true;
            list.clear();
            loadData(true);
        }
    }

    // 添加返回键处理
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // 关闭当前Activity
    }
}