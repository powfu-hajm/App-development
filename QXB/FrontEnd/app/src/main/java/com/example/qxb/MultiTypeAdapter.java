package com.example.qxb;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qxb.models.Article;
import java.util.List;

public class MultiTypeAdapter extends RecyclerView.Adapter<MultiTypeAdapter.ContentViewHolder> {

    private List<Article> contentList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Article item);
    }

    public MultiTypeAdapter(List<Article> list, OnItemClickListener listener) {
        this.contentList = list;
        this.listener = listener;
        Log.d("MultiTypeAdapter", "适配器创建，初始数据量: " + (list != null ? list.size() : 0));
    }

    /**
     * 更新数据 - 现在只用于设置新的数据源
     */
    public void setData(List<Article> newList) {
        this.contentList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        Log.d("MultiTypeAdapter", "onCreateViewHolder被调用");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_article, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ContentViewHolder holder,
            int position
    ) {
        Log.d("MultiTypeAdapter", "onBindViewHolder位置: " + position + ", 总数据量: " + getItemCount());

        if (position < contentList.size()) {
            Article item = contentList.get(position);
            holder.bind(item, listener);
            Log.d("MultiTypeAdapter", "绑定文章: id=" + item.getId() + ", title=" + item.getTitle());
        } else {
            Log.w("MultiTypeAdapter", "位置超出范围: " + position);
        }
    }

    @Override
    public int getItemCount() {
        int count = contentList != null ? contentList.size() : 0;
        Log.d("MultiTypeAdapter", "getItemCount返回: " + count);
        return count;
    }

    public static class ContentViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvSummary, tvReadTime, tvCategory;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSummary = itemView.findViewById(R.id.tvSummary);
            tvReadTime = itemView.findViewById(R.id.tvReadTime);
            tvCategory = itemView.findViewById(R.id.tvCategory);

            // 检查视图是否找到
            if (tvTitle == null) Log.e("MultiTypeAdapter", "❌ tvTitle未找到！");
            if (tvSummary == null) Log.e("MultiTypeAdapter", "❌ tvSummary未找到！");
            if (tvReadTime == null) Log.e("MultiTypeAdapter", "❌ tvReadTime未找到！");
            if (tvCategory == null) Log.e("MultiTypeAdapter", "❌ tvCategory未找到！");

            if (tvTitle != null && tvSummary != null && tvReadTime != null && tvCategory != null) {
                Log.d("MultiTypeAdapter", "✅ 所有视图都正确找到");
            }
        }

        public void bind(Article item, OnItemClickListener listener) {
            if (item == null) {
                Log.w("MultiTypeAdapter", "bind: item为null");
                return;
            }

            Log.d("MultiTypeAdapter", "绑定文章: id=" + item.getId() + ", title=" + item.getTitle());

            // 设置标题
            if (item.getTitle() != null) {
                tvTitle.setText(item.getTitle());
                tvTitle.setVisibility(View.VISIBLE);
            } else {
                tvTitle.setText("无标题");
                tvTitle.setVisibility(View.VISIBLE);
            }

            // 设置摘要
            if (item.getSummary() != null && !item.getSummary().isEmpty()) {
                tvSummary.setText(item.getSummary());
                tvSummary.setVisibility(View.VISIBLE);
            } else {
                tvSummary.setVisibility(View.GONE);
            }

            // 设置阅读数
            if (item.getReadCount() != null) {
                tvReadTime.setText(item.getReadCount() + "阅读");
                tvReadTime.setVisibility(View.VISIBLE);
            } else {
                tvReadTime.setText("0阅读");
                tvReadTime.setVisibility(View.VISIBLE);
            }

            // 设置分类
            String categoryText = null;
            if (item.getSource() != null && !item.getSource().isEmpty()) {
                categoryText = item.getSource();
            } else if (item.getType() != null && !item.getType().isEmpty()) {
                categoryText = item.getType();
            } else if ("action_more".equals(item.getSource())) {
                categoryText = "查看更多";
            } else if ("每日一句".equals(item.getSource())) {
                categoryText = "每日一句";
            }

            if (categoryText != null) {
                tvCategory.setText(categoryText);
                tvCategory.setVisibility(View.VISIBLE);
            } else {
                tvCategory.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    Log.d("MultiTypeAdapter", "文章被点击: " + item.getTitle());
                    listener.onItemClick(item);
                }
            });
        }
    }
}