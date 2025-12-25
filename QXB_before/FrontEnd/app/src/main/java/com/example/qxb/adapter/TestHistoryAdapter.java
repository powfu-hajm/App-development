package com.example.qxb.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qxb.R;
import com.example.qxb.models.test.TestResult;

import java.util.ArrayList;
import java.util.List;

public class TestHistoryAdapter extends RecyclerView.Adapter<TestHistoryAdapter.ViewHolder> {

    private List<TestResult> historyList = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(TestResult result);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<TestResult> data) {
        this.historyList = data != null ? data : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_test_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TestResult result = historyList.get(position);
        holder.bind(result);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPaperTitle, tvTestTime, tvScore, tvResultTitle, tvResultDescription;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPaperTitle = itemView.findViewById(R.id.tvPaperTitle);
            tvTestTime = itemView.findViewById(R.id.tvTestTime);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvResultTitle = itemView.findViewById(R.id.tvResultTitle);
            tvResultDescription = itemView.findViewById(R.id.tvResultDescription);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(historyList.get(pos));
                }
            });
        }

        void bind(TestResult result) {
            tvPaperTitle.setText(result.getPaperTitle() != null ? result.getPaperTitle() : "心理测试");
            tvTestTime.setText(formatTime(result.getTestTime()));
            tvScore.setText(String.valueOf(result.getTotalScore() != null ? result.getTotalScore() : 0));
            tvResultTitle.setText(result.getResultTitle() != null ? result.getResultTitle() : "");
            tvResultDescription.setText(result.getResultDescription() != null ? result.getResultDescription() : "");

            // 根据结果等级设置颜色
            int color = getResultColor(result.getResultLevel());
            tvResultTitle.setTextColor(color);
        }

        private String formatTime(String testTime) {
            if (testTime == null) return "";
            // 如果时间格式是 "2025-12-20T16:19:13"，只取日期部分
            if (testTime.contains("T")) {
                return testTime.split("T")[0];
            }
            // 如果时间格式是 "2025-12-20 16:19:13"，只取日期部分
            if (testTime.contains(" ")) {
                return testTime.split(" ")[0];
            }
            return testTime;
        }

        private int getResultColor(String level) {
            if (level == null) return 0xFF666666;
            switch (level.toLowerCase()) {
                case "normal":
                case "low":
                    return 0xFF4CAF50; // 绿色
                case "mild":
                case "moderate":
                    return 0xFFFF9800; // 橙色
                case "severe":
                case "high":
                    return 0xFFFF6B6B; // 红色
                default:
                    return 0xFF666666; // 灰色
            }
        }
    }
}
