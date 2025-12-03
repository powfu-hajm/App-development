package com.example.qxb.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qxb.R;
import com.example.qxb.Article;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private List<Article> articleList;

    public interface OnItemClickListener {
        void onItemClick(Article article);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public ArticleAdapter(List<Article> articleList) {
        this.articleList = articleList;
    }

    /**
     * 更新数据的方法
     */
    public void updateData(List<Article> newArticleList) {
        this.articleList = newArticleList;
        notifyDataSetChanged();
    }

    /**
     * 添加单个文章
     */
    public void addArticle(Article article) {
        this.articleList.add(article);
        notifyItemInserted(articleList.size() - 1);
    }

    /**
     * 移除文章
     */
    public void removeArticle(int position) {
        if (position >= 0 && position < articleList.size()) {
            articleList.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (articleList == null || articleList.size() <= position) {
            return;
        }

        Article article = articleList.get(position);
        holder.bind(article, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return articleList != null ? articleList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // 声明视图变量 - 现在这些ID在布局中存在了
        TextView tvTitle, tvSummary, tvReadTime, tvCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // 初始化视图 - 添加空值检查
            try {
                tvTitle = itemView.findViewById(R.id.tvTitle);
            } catch (Exception e) {
                // 处理ID不存在的情况
                System.out.println("警告: tvTitle 视图ID未找到");
            }

            try {
                tvSummary = itemView.findViewById(R.id.tvSummary);
            } catch (Exception e) {
                System.out.println("警告: tvSummary 视图ID未找到");
            }

            try {
                tvReadTime = itemView.findViewById(R.id.tvReadTime);
            } catch (Exception e) {
                System.out.println("警告: tvReadTime 视图ID未找到");
            }

            try {
                tvCategory = itemView.findViewById(R.id.tvCategory);
            } catch (Exception e) {
                System.out.println("警告: tvCategory 视图ID未找到");
            }
        }

        /**
         * 绑定数据到视图
         */
        public void bind(Article article, OnItemClickListener listener) {
            if (article == null) return;

            // 安全设置文本内容
            if (tvTitle != null) {
                tvTitle.setText(article.getTitle() != null ? article.getTitle() : "");
            }

            if (tvSummary != null) {
                tvSummary.setText(article.getSummary() != null ? article.getSummary() : "");
            }

            if (tvReadTime != null) {
                tvReadTime.setText(article.getReadTime() != null ? article.getReadTime() : "");
            }

            if (tvCategory != null) {
                tvCategory.setText(article.getCategory() != null ? article.getCategory() : "");
            }

            // 设置点击监听器
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(article);
                }
            });
        }
    }
}