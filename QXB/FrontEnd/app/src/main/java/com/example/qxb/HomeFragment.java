package com.example.qxb;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qxb.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private CardView cardChat, cardDiary, cardTest;
    private RecyclerView recyclerViewArticles;
    private MultiTypeAdapter multiTypeAdapter;
    private List<ContentItem> contentList = new ArrayList<>();

    // 时间显示相关
    private TextView tvDate, tvGreeting;
    private Handler timeHandler;
    private Runnable timeRunnable;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        setupClickListeners();
        setupContentList();
        startTimeUpdates();

        return view;
    }

    private void initViews(View view) {
        // 初始化功能卡片
        cardChat = view.findViewById(R.id.card_chat);
        cardDiary = view.findViewById(R.id.card_diary);
        cardTest = view.findViewById(R.id.card_test);
        recyclerViewArticles = view.findViewById(R.id.recyclerViewArticles);

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

        if (tvDate == null) {
            System.out.println("警告: tvDate 视图未在布局中找到");
        }
        if (tvGreeting == null) {
            System.out.println("警告: tvGreeting 视图未在布局中找到");
        }
    }

    /**
     * 启动实时时间更新
     */
    private void startTimeUpdates() {
        if (tvDate == null && tvGreeting == null) {
            return;
        }

        timeHandler = new Handler();
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                updateCurrentTime();
                updateGreeting();
                timeHandler.postDelayed(this, 60000);
            }
        };

        updateCurrentTime();
        updateGreeting();
        timeHandler.postDelayed(timeRunnable, 60000);
    }

    /**
     * 更新当前时间显示
     */
    private void updateCurrentTime() {
        if (tvDate == null) return;

        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 EEEE", Locale.CHINA);
                    String currentDate = dateFormat.format(new Date());
                    tvDate.setText("今天是" + currentDate);
                }
            });
        }
    }

    /**
     * 根据时间更新问候语
     */
    private void updateGreeting() {
        if (tvGreeting == null) return;

        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH", Locale.CHINA);
                    int hour = Integer.parseInt(timeFormat.format(new Date()));

                    String greeting;
                    if (hour >= 5 && hour < 12) {
                        greeting = "早上好，用户";
                    } else if (hour >= 12 && hour < 18) {
                        greeting = "下午好，用户";
                    } else {
                        greeting = "晚上好，用户";
                    }

                    tvGreeting.setText(greeting);
                }
            });
        }
    }

    private void setupContentList() {
        if (recyclerViewArticles != null) {
            initContentData();

            recyclerViewArticles.setLayoutManager(new LinearLayoutManager(getContext()));
            multiTypeAdapter = new MultiTypeAdapter(contentList, new MultiTypeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(ContentItem item) {
                    handleContentClick(item);
                }
            });
            recyclerViewArticles.setAdapter(multiTypeAdapter);
        }
    }

    /**
     * 初始化内容数据
     */
    private void initContentData() {
        contentList.clear();

        // 1. 文章类型内容
        ContentItem article1 = new ContentItem(
                "1",
                "工作多年突然觉得'没意思'？或许你该寻找自己的人生主线了",
                "探索职业倦怠背后的心理因素，重新发现工作的意义和价值...",
                "article"
        );
        article1.setReadTime("5分钟阅读");
        article1.setCategory("心理成长");
        contentList.add(article1);

        // 2. 如何有效管理压力？5个实用技巧帮你重获内心平静
        ContentItem article2 = new ContentItem(
                "2",
                "如何有效管理压力？5个实用技巧帮你重获内心平静",
                "在现代快节奏生活中，压力管理成为每个人必备的技能...",
                "article"
        );
        article2.setReadTime("3分钟阅读");
        article2.setCategory("压力管理");
        contentList.add(article2);

        // 3. 改善睡眠质量的7个科学方法
        ContentItem article3 = new ContentItem(
                "3",
                "改善睡眠质量的7个科学方法",
                "良好的睡眠是心理健康的基础，学习科学的睡眠改善技巧...",
                "article"
        );
        article3.setReadTime("4分钟阅读");
        article3.setCategory("睡眠健康");
        contentList.add(article3);

        // 4. 重复的文章（图片中显示有重复内容）
        ContentItem article4 = new ContentItem(
                "4",
                "工作多年突然觉得'没意思'？或许你该寻找自己的人生主线了",
                "探索职业倦怠背后的心理因素，重新发现工作的意义和价值...",
                "article"
        );
        article4.setReadTime("5分钟阅读");
        article4.setCategory("心理成长");
        contentList.add(article4);

        // 5. 心理日签（特殊类型）
        ContentItem dailyQuote = new ContentItem(
                "5",
                "心理日签",
                "什么是真正的爱？这是我见过最好的答案\n爱是接纳不完美，是给予自由，是在彼此陪伴中共同成长。",
                "daily_quote"
        );
        dailyQuote.setCategory("每日一句");
        contentList.add(dailyQuote);

        // 6. 视频类型内容
        ContentItem videoItem = new ContentItem(
                "6",
                "正念冥想入门指南 - 10分钟放松身心",
                "跟随专业导师学习基础的正念冥想技巧，缓解压力焦虑...",
                "video"
        );
        videoItem.setReadTime("10分钟视频");
        videoItem.setCategory("冥想练习");
        videoItem.setMediaUrl("https://example.com/video1.mp4");
        contentList.add(videoItem);

        // 7. 音频类型内容
        ContentItem audioItem = new ContentItem(
                "7",
                "深度放松音乐 - 阿尔法脑波舒缓焦虑",
                "专业的放松音乐，帮助大脑进入放松的阿尔法波状态...",
                "audio"
        );
        audioItem.setReadTime("15分钟音频");
        audioItem.setCategory("音乐疗愈");
        audioItem.setMediaUrl("https://example.com/audio1.mp3");
        contentList.add(audioItem);

        // 通知适配器数据更新
        if (multiTypeAdapter != null) {
            multiTypeAdapter.notifyDataSetChanged();
        }
    }

    private void setupClickListeners() {
        if (cardChat != null) {
            cardChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openChatFunction();
                }
            });
        }

        if (cardDiary != null) {
            cardDiary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDiaryFunction();
                }
            });
        }

        if (cardTest != null) {
            cardTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openTestFunction();
                }
            });
        }
    }

    /**
     * 打开AI倾诉功能
     */
    private void openChatFunction() {
        // 这里可以跳转到聊天页面或显示对话框
        showMessage("AI倾诉功能开发中...");
    }

    /**
     * 打开心情日记功能
     */
    private void openDiaryFunction() {
        // 跳转到日记页面
        Intent intent = new Intent(getActivity(), DiaryActivity.class);
        startActivity(intent);
    }

    /**
     * 打开心理测试功能
     */
    private void openTestFunction() {
        // 跳转到测试页面
        Intent intent = new Intent(getActivity(), TestActivity.class);
        startActivity(intent);
    }

    /**
     * 显示提示消息
     */
    private void showMessage(String message) {
        if (getActivity() != null) {
            android.widget.Toast.makeText(getActivity(), message, android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 根据内容类型处理点击事件
     */
    private void handleContentClick(ContentItem item) {
        if (item == null) return;

        Intent intent = null;

        switch (item.getType()) {
            case "daily_quote":
                // 心理日签特殊处理
                intent = new Intent(getActivity(), ArticleDetailActivity.class);
                intent.putExtra("article_title", item.getTitle());
                intent.putExtra("article_content", item.getDescription());
                intent.putExtra("read_time", "1分钟阅读");
                intent.putExtra("category", "每日一句");
                break;

            case "video":
                intent = new Intent(getActivity(), VideoPlayerActivity.class);
                intent.putExtra("video_item", item);
                break;

            case "audio":
                // 暂时用视频播放器代替
                intent = new Intent(getActivity(), VideoPlayerActivity.class);
                intent.putExtra("video_item", item);
                break;

            case "article":
            default:
                intent = new Intent(getActivity(), ArticleDetailActivity.class);
                intent.putExtra("article_title", item.getTitle());
                intent.putExtra("article_content", getArticleFullContent(item.getTitle()));
                intent.putExtra("read_time", item.getReadTime());
                intent.putExtra("category", item.getCategory());
                break;
        }

        if (intent != null) {
            // 添加公共参数
            intent.putExtra("content_id", item.getId());
            intent.putExtra("content_type", item.getType());
            startActivity(intent);
        }
    }

    /**
     * 根据标题获取完整的文章内容
     */
    private String getArticleFullContent(String title) {
        switch (title) {
            case "工作多年突然觉得'没意思'？或许你该寻找自己的人生主线了":
                return "工作多年后，很多人会经历所谓的'职业倦怠期'。这种感受并非个例，而是现代职场中普遍存在的现象。\n\n" +
                        "## 职业倦怠的表现\n\n" +
                        "1. **情感枯竭**：对工作失去热情，感到精疲力尽\n" +
                        "2. **去个性化**：对同事和客户变得冷漠、疏远\n" +
                        "3. **个人成就感降低**：怀疑自己的工作价值和能力\n\n" +
                        "## 重新发现工作意义的方法\n\n" +
                        "### 1. 反思核心价值观\n" +
                        "问自己：什么对我真正重要？我的工作如何与个人价值观对齐？\n\n" +
                        "### 2. 设定新的挑战\n" +
                        "寻找工作中的学习机会，设定短期可实现的目标。\n\n" +
                        "### 3. 建立支持网络\n" +
                        "与志同道合的同事建立联系，分享经验和感受。\n\n" +
                        "### 4. 工作与生活平衡\n" +
                        "确保有足够的休息和娱乐时间，培养工作外的兴趣爱好。\n\n" +
                        "## 结语\n\n" +
                        "职业倦怠不是终点，而是重新评估和调整职业道路的契机。";

            case "如何有效管理压力？5个实用技巧帮你重获内心平静":
                return "在现代快节奏生活中，压力管理成为每个人必备的技能。以下是5个实用技巧：\n\n" +
                        "### 1. 深呼吸练习\n" +
                        "每天花5分钟进行深呼吸，帮助身体放松。\n\n" +
                        "### 2. 时间管理\n" +
                        "合理安排时间，避免过度承诺。\n\n" +
                        "### 3. 运动释放\n" +
                        "定期运动可以帮助释放压力荷尔蒙。\n\n" +
                        "### 4. 社交支持\n" +
                        "与朋友家人分享感受，获得情感支持。\n\n" +
                        "### 5. 正念冥想\n" +
                        "通过冥想练习提高当下的觉察能力。";

            case "改善睡眠质量的7个科学方法":
                return "良好的睡眠是心理健康的基础。以下是改善睡眠的科学方法：\n\n" +
                        "### 1. 规律作息\n" +
                        "每天固定时间上床和起床。\n\n" +
                        "### 2. 创造舒适环境\n" +
                        "保持卧室安静、黑暗、凉爽。\n\n" +
                        "### 3. 避免咖啡因\n" +
                        "睡前6小时避免摄入咖啡因。\n\n" +
                        "### 4. 限制屏幕时间\n" +
                        "睡前1小时避免使用电子设备。\n\n" +
                        "### 5. 放松技巧\n" +
                        "睡前进行放松练习。\n\n" +
                        "### 6. 适度运动\n" +
                        "白天适量运动有助于睡眠。\n\n" +
                        "### 7. 注意饮食\n" +
                        "避免睡前大量进食。";

            default:
                return "这是" + title + "的详细内容。文章正在完善中，敬请期待更多精彩内容...";
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 页面恢复时更新时间
        updateCurrentTime();
        updateGreeting();
    }

    @Override
    public void onPause() {
        super.onPause();
        // 页面暂停时停止时间更新
        if (timeHandler != null && timeRunnable != null) {
            timeHandler.removeCallbacks(timeRunnable);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 清理资源
        if (timeHandler != null && timeRunnable != null) {
            timeHandler.removeCallbacks(timeRunnable);
        }
    }
}