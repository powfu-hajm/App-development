package com.example.qxb.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qxb.R;
import com.example.qxb.models.entity.Diary;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {

    private List<Diary> diaryList;
    private OnDeleteClickListener onDeleteClickListener;
    private OnEditClickListener onEditClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public interface OnEditClickListener {
        void onEditClick(int position);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.onEditClickListener = listener;
    }

    public DiaryAdapter(List<Diary> diaryList) {
        this.diaryList = diaryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_diary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Diary diary = diaryList.get(position);
        holder.bind(diary);

        // 设置删除按钮点击事件
        holder.btnDelete.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(position);
            }
        });

        // 设置编辑按钮点击事件
        holder.btnEdit.setOnClickListener(v -> {
            if (onEditClickListener != null) {
                onEditClickListener.onEditClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return diaryList == null ? 0 : diaryList.size();
    }

    public void updateData(List<Diary> newDiaryList) {
        this.diaryList = newDiaryList;
        notifyDataSetChanged();
    }

    public Diary getItem(int position) {
        if (position >= 0 && position < diaryList.size()) {
            return diaryList.get(position);
        }
        return null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate;
        private TextView tvMood;
        private TextView tvContent;
        private TextView tvUpdateTime;
        private ImageButton btnEdit;
        private ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvMood = itemView.findViewById(R.id.tvMood);
            tvContent = itemView.findViewById(R.id.tvContent);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            // 如果有单独的更新时间显示，可以添加这个TextView
            // tvUpdateTime = itemView.findViewById(R.id.tvUpdateTime);
        }

        public void bind(Diary diary) {
            // 优先显示更新时间，如果没有则显示创建时间
            String displayTime = diary.getUpdateTime() != null ? diary.getUpdateTime() : diary.getCreateTime();
            String formattedDate = formatDate(displayTime);
            tvDate.setText(formattedDate);

            // 如果有单独的更新时间显示，可以这样设置
            if (diary.getUpdateTime() != null && !diary.getUpdateTime().equals(diary.getCreateTime())) {
                // 如果更新时间与创建时间不同，显示编辑时间
                String updateTimeText = "编辑于 " + formatShortDate(diary.getUpdateTime());
                // 如果有tvUpdateTime就设置，否则可以合并显示在主日期中
                if (tvUpdateTime != null) {
                    tvUpdateTime.setText(updateTimeText);
                    tvUpdateTime.setVisibility(View.VISIBLE);
                } else {
                    // 合并显示：创建时间 + (编辑时间)
                    String combinedText = formattedDate + " (编辑于 " + formatShortDate(diary.getUpdateTime()) + ")";
                    tvDate.setText(combinedText);
                }
            } else {
                if (tvUpdateTime != null) {
                    tvUpdateTime.setVisibility(View.GONE);
                }
            }

            tvMood.setText("心情: " + (diary.getMoodTag() != null ? diary.getMoodTag() : "暂无"));
            tvContent.setText(diary.getContent() != null ? diary.getContent() : "");
        }

        private String formatDate(String dateString) {
            // 添加空值检查
            if (dateString == null || dateString.isEmpty()) {
                return "未知时间";
            }

            try {
                // 尝试解析带时间的格式
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault());
                Date date = inputFormat.parse(dateString);
                return outputFormat.format(date);
            } catch (ParseException e) {
                // 如果解析失败，尝试其他格式
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault());
                    Date date = inputFormat.parse(dateString);
                    return outputFormat.format(date);
                } catch (ParseException e2) {
                    try {
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
                        Date date = inputFormat.parse(dateString);
                        return outputFormat.format(date);
                    } catch (ParseException e3) {
                        return dateString; // 返回原始字符串
                    }
                }
            }
        }

        // 新增：短日期格式化方法，用于显示编辑时间
        private String formatShortDate(String dateString) {
            if (dateString == null || dateString.isEmpty()) {
                return "";
            }

            try {
                // 尝试解析带时间的格式
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MM月dd日 HH:mm", Locale.getDefault());
                Date date = inputFormat.parse(dateString);
                return outputFormat.format(date);
            } catch (ParseException e) {
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    SimpleDateFormat outputFormat = new SimpleDateFormat("MM月dd日 HH:mm", Locale.getDefault());
                    Date date = inputFormat.parse(dateString);
                    return outputFormat.format(date);
                } catch (ParseException e2) {
                    try {
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        SimpleDateFormat outputFormat = new SimpleDateFormat("MM月dd日", Locale.getDefault());
                        Date date = inputFormat.parse(dateString);
                        return outputFormat.format(date);
                    } catch (ParseException e3) {
                        // 返回原始字符串的后半部分
                        if (dateString.length() > 10) {
                            return dateString.substring(5); // 返回 "MM-dd" 部分
                        }
                        return dateString;
                    }
                }
            }
        }
    }
}