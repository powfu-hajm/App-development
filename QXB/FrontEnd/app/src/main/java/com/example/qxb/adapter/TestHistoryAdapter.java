package com.example.qxb.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qxb.R;
import com.example.qxb.models.test.TestResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TestHistoryAdapter extends RecyclerView.Adapter<TestHistoryAdapter.ViewHolder> {

    private List<TestResult> dataList = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(TestResult result);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setData(List<TestResult> list) {
        this.dataList = list != null ? list : new ArrayList<>();
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
        TestResult result = dataList.get(position);
        holder.bind(result);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(result);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvPaperTitle;
        private final TextView tvTestTime;
        private final ImageView ivResultIcon;
        private final TextView tvResultTitle;
        private final TextView tvResultLevel;
        private final TextView tvScore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPaperTitle = itemView.findViewById(R.id.tvPaperTitle);
            tvTestTime = itemView.findViewById(R.id.tvTestTime);
            ivResultIcon = itemView.findViewById(R.id.ivResultIcon);
            tvResultTitle = itemView.findViewById(R.id.tvResultTitle);
            tvResultLevel = itemView.findViewById(R.id.tvResultLevel);
            tvScore = itemView.findViewById(R.id.tvScore);
        }

        public void bind(TestResult result) {
            tvPaperTitle.setText(result.getPaperTitle());
            tvTestTime.setText(formatDate(result.getTestTime()));
            tvResultTitle.setText(result.getResultTitle());
            tvScore.setText(String.valueOf(result.getTotalScore()));

            // 根据结果等级设置图标、颜色和文本
            String level = result.getResultLevel();
            int iconRes;
            int colorRes;
            String levelText;

            switch (level != null ? level : "") {
                case "normal":
                    iconRes = R.drawable.ic_result_normal;
                    colorRes = R.color.result_normal;
                    levelText = "正常";
                    break;
                case "mild":
                    iconRes = R.drawable.ic_result_mild;
                    colorRes = R.color.result_mild;
                    levelText = "轻度";
                    break;
                case "moderate":
                    iconRes = R.drawable.ic_result_moderate;
                    colorRes = R.color.result_moderate;
                    levelText = "中度";
                    break;
                case "severe":
                    iconRes = R.drawable.ic_result_severe;
                    colorRes = R.color.result_severe;
                    levelText = "重度";
                    break;
                default:
                    // MBTI等其他类型测试
                    iconRes = R.drawable.ic_result_normal;
                    colorRes = R.color.colorPrimary;
                    levelText = level != null ? level : "";
            }

            ivResultIcon.setImageResource(iconRes);
            tvScore.setTextColor(ContextCompat.getColor(itemView.getContext(), colorRes));
            tvResultLevel.setText(levelText);
        }

        private String formatDate(String dateString) {
            if (dateString == null || dateString.isEmpty()) {
                return "未知时间";
            }

            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                Date date = inputFormat.parse(dateString);
                return outputFormat.format(date);
            } catch (ParseException e) {
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    Date date = inputFormat.parse(dateString);
                    return outputFormat.format(date);
                } catch (ParseException e2) {
                    return dateString;
                }
            }
        }
    }
}
