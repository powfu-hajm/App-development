package com.example.qxb;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MultiTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ARTICLE = 0;
    private static final int TYPE_VIDEO = 1;
    private static final int TYPE_AUDIO = 2;
    private static final int TYPE_TEST = 3;
    private static final int TYPE_COURSE = 4;
    private static final int TYPE_DAILY_QUOTE = 5;

    private List<ContentItem> contentList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ContentItem item);
    }

    public MultiTypeAdapter(List<ContentItem> contentList, OnItemClickListener listener) {
        this.contentList = contentList;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (contentList == null || contentList.size() <= position) {
            return TYPE_ARTICLE;
        }

        ContentItem item = contentList.get(position);
        String type = item.getType();
        if (type == null) {
            return TYPE_ARTICLE;
        }

        switch (type) {
            case "video":
                return TYPE_VIDEO;
            case "audio":
                return TYPE_AUDIO;
            case "test":
                return TYPE_TEST;
            case "course":
                return TYPE_COURSE;
            case "daily_quote":
                return TYPE_DAILY_QUOTE;
            default:
                return TYPE_ARTICLE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TYPE_VIDEO:
                return new VideoViewHolder(inflater.inflate(R.layout.item_video, parent, false));
            case TYPE_AUDIO:
                return new AudioViewHolder(inflater.inflate(R.layout.item_audio, parent, false));
            case TYPE_TEST:
                return new TestViewHolder(inflater.inflate(R.layout.item_test, parent, false));
            case TYPE_COURSE:
                return new CourseViewHolder(inflater.inflate(R.layout.item_course, parent, false));
            case TYPE_DAILY_QUOTE:
                return new DailyQuoteViewHolder(inflater.inflate(R.layout.item_daily_quote, parent, false));
            default:
                return new ArticleViewHolder(inflater.inflate(R.layout.item_article, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (contentList == null || contentList.size() <= position) {
            return;
        }

        ContentItem item = contentList.get(position);

        switch (holder.getItemViewType()) {
            case TYPE_ARTICLE:
                ((ArticleViewHolder) holder).bind(item, listener);
                break;
            case TYPE_VIDEO:
                ((VideoViewHolder) holder).bind(item, listener);
                break;
            case TYPE_AUDIO:
                ((AudioViewHolder) holder).bind(item, listener);
                break;
            case TYPE_TEST:
                ((TestViewHolder) holder).bind(item, listener);
                break;
            case TYPE_COURSE:
                ((CourseViewHolder) holder).bind(item, listener);
                break;
            case TYPE_DAILY_QUOTE:
                ((DailyQuoteViewHolder) holder).bind(item, listener);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return contentList != null ? contentList.size() : 0;
    }

    // 更新数据的方法
    public void updateData(List<ContentItem> newContentList) {
        this.contentList = newContentList;
        notifyDataSetChanged();
    }

    // ========== 各种类型的ViewHolder ==========

    // 文章ViewHolder
    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSummary, tvReadTime, tvCategory;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSummary = itemView.findViewById(R.id.tvSummary);
            tvReadTime = itemView.findViewById(R.id.tvReadTime);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }

        public void bind(ContentItem item, OnItemClickListener listener) {
            if (item == null) return;

            // 安全设置文本内容
            if (tvTitle != null) {
                tvTitle.setText(item.getTitle() != null ? item.getTitle() : "文章标题");
            }

            if (tvSummary != null) {
                tvSummary.setText(item.getDescription() != null ? item.getDescription() : "文章摘要");
            }

            if (tvReadTime != null) {
                tvReadTime.setText(item.getReadTime() != null ? item.getReadTime() : "5分钟阅读");
            }

            if (tvCategory != null) {
                tvCategory.setText(item.getCategory() != null ? item.getCategory() : "心理成长");
            }

            // 设置点击监听器
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    // 视频ViewHolder
    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView tvVideoTitle, tvVideoDesc, tvVideoMeta;
        TextView ivPlayButton;  // 修改为TextView类型

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVideoTitle = itemView.findViewById(R.id.tvVideoTitle);
            tvVideoDesc = itemView.findViewById(R.id.tvVideoDesc);
            tvVideoMeta = itemView.findViewById(R.id.tvVideoMeta);
            ivPlayButton = itemView.findViewById(R.id.ivPlayButton);  // 使用新的ID
        }

        public void bind(ContentItem item, OnItemClickListener listener) {
            if (item == null) return;

            if (tvVideoTitle != null) {
                tvVideoTitle.setText(item.getTitle() != null ? item.getTitle() : "视频标题");
            }

            if (tvVideoDesc != null) {
                tvVideoDesc.setText(item.getDescription() != null ? item.getDescription() : "视频描述");
            }

            if (tvVideoMeta != null) {
                String meta = (item.getReadTime() != null ? item.getReadTime() : "10分钟视频") +
                        " · " + (item.getCategory() != null ? item.getCategory() : "冥想练习");
                tvVideoMeta.setText(meta);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }


    // 音频ViewHolder
    public static class AudioViewHolder extends RecyclerView.ViewHolder {
        TextView tvAudioTitle, tvAudioDesc, tvAudioDuration;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAudioTitle = itemView.findViewById(R.id.tvAudioTitle);
            tvAudioDesc = itemView.findViewById(R.id.tvAudioDesc);
            tvAudioDuration = itemView.findViewById(R.id.tvAudioDuration);
        }

        public void bind(ContentItem item, OnItemClickListener listener) {
            if (item == null) return;

            if (tvAudioTitle != null) {
                tvAudioTitle.setText(item.getTitle() != null ? item.getTitle() : "音频标题");
            }

            if (tvAudioDesc != null) {
                tvAudioDesc.setText(item.getDescription() != null ? item.getDescription() : "音频描述");
            }

            if (tvAudioDuration != null) {
                tvAudioDuration.setText(item.getReadTime() != null ? item.getReadTime() : "15分钟音频");
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    // 测试ViewHolder
    public static class TestViewHolder extends RecyclerView.ViewHolder {
        TextView tvTestTitle, tvTestDesc, tvTestDuration;

        public TestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTestTitle = itemView.findViewById(R.id.tvTestTitle);
            tvTestDesc = itemView.findViewById(R.id.tvTestDesc);
            tvTestDuration = itemView.findViewById(R.id.tvTestDuration);
        }

        public void bind(ContentItem item, OnItemClickListener listener) {
            if (item == null) return;

            if (tvTestTitle != null) {
                tvTestTitle.setText(item.getTitle() != null ? item.getTitle() : "测试标题");
            }

            if (tvTestDesc != null) {
                tvTestDesc.setText(item.getDescription() != null ? item.getDescription() : "测试描述");
            }

            if (tvTestDuration != null) {
                tvTestDuration.setText(item.getReadTime() != null ? item.getReadTime() : "3分钟测试");
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    // 课程ViewHolder
    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseTitle, tvCourseDesc, tvCourseDuration;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseTitle = itemView.findViewById(R.id.tvCourseTitle);
            tvCourseDesc = itemView.findViewById(R.id.tvCourseDesc);
            tvCourseDuration = itemView.findViewById(R.id.tvCourseDuration);
        }

        public void bind(ContentItem item, OnItemClickListener listener) {
            if (item == null) return;

            if (tvCourseTitle != null) {
                tvCourseTitle.setText(item.getTitle() != null ? item.getTitle() : "课程标题");
            }

            if (tvCourseDesc != null) {
                tvCourseDesc.setText(item.getDescription() != null ? item.getDescription() : "课程描述");
            }

            if (tvCourseDuration != null) {
                tvCourseDuration.setText(item.getReadTime() != null ? item.getReadTime() : "系列课程");
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    // 心理日签ViewHolder
    public static class DailyQuoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuoteTitle, tvQuoteContent, tvQuoteCategory;

        public DailyQuoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuoteTitle = itemView.findViewById(R.id.tvQuoteTitle);
            tvQuoteContent = itemView.findViewById(R.id.tvQuoteContent);
            tvQuoteCategory = itemView.findViewById(R.id.tvQuoteCategory);
        }

        public void bind(ContentItem item, OnItemClickListener listener) {
            if (item == null) return;

            if (tvQuoteTitle != null) {
                tvQuoteTitle.setText(item.getTitle() != null ? item.getTitle() : "心理日签");
            }

            if (tvQuoteContent != null) {
                tvQuoteContent.setText(item.getDescription() != null ? item.getDescription() : "每日一句");
            }

            if (tvQuoteCategory != null) {
                tvQuoteCategory.setText(item.getCategory() != null ? item.getCategory() : "每日一句");
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}