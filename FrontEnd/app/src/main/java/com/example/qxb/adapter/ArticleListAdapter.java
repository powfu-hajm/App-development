package com.example.qxb.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.TextView;

import java.util.List;
import com.example.qxb.R;

import com.example.qxb.models.Article;

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ViewHolder> {

    private List<Article> list;

    public ArticleListAdapter(List<Article> list){
        this.list = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        public ViewHolder(View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_article,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        Article article = list.get(position);
        holder.title.setText(article.getTitle());
    }

    @Override
    public int getItemCount(){
        return list.size();
    }
}
