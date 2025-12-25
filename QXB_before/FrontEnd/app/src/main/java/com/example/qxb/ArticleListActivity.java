package com.example.qxb;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.qxb.adapter.ArticleAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.ArrayList;
import java.util.List;

import com.example.qxb.model.PageResponse;
import com.example.qxb.models.Article;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArticleAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;

    private List<Article> list = new ArrayList<>();

    // ---------- 分页参数 ----------
    private int pageNum = 1;
    private final int pageSize = 10;
    private boolean isLoading = false;      // 是否正在加载
    private boolean hasNextPage = true;     // 是否还有下一页


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        initView();          // 初始化控件
        loadData(true);      // 首次加载，显示 loading
        initListener();      // 监听滚动 & 刷新
    }


    private void initView() {
        recyclerView = findViewById(R.id.articleRecyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        progressBar = findViewById(R.id.progressBar);

        adapter = new ArticleAdapter(this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    private void initListener() {

        // ---------- 下拉刷新 ----------
        swipeRefreshLayout.setOnRefreshListener(() -> {
            pageNum = 1;
            hasNextPage = true;
            list.clear();
            loadData(false);
        });

        // ---------- RecyclerView 滚动到底加载更多 ----------
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);

                LinearLayoutManager manager = (LinearLayoutManager) rv.getLayoutManager();

                if (!isLoading && hasNextPage) {
                    if (manager != null &&
                            manager.findLastVisibleItemPosition() == list.size() - 1) {
                        pageNum++;
                        loadData(false);
                    }
                }
            }
        });
    }


    /**
     * 加载分页数据
     */
    private void loadData(boolean firstLoad) {
        if (isLoading) return;

        isLoading = true;

        if (firstLoad)
            progressBar.setVisibility(View.VISIBLE);

        ApiService api = RetrofitClient.getApiService();
        Call<PageResponse<Article>> call =
                api.getArticleList(pageNum, pageSize);

        call.enqueue(new Callback<PageResponse<Article>>() {
            @Override
            public void onResponse(Call<PageResponse<Article>> call, Response<PageResponse<Article>> response) {

                isLoading = false;
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (response.body() != null) {

                    PageResponse<Article> result = response.body();

                    List<Article> newRecords = result.getData(); //获取列表

                    list.addAll(newRecords);
                    adapter.notifyDataSetChanged();

                    hasNextPage = newRecords.size() == pageSize;

                    isLoading = false;
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);

                }
            }

            @Override
            public void onFailure(Call<PageResponse<Article>> call, Throwable t) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(ArticleListActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
