package org.example.emotionbackend.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import okhttp3.*;
import org.example.emotionbackend.entity.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AIService {

    @Value("${ai.api.key}")
    private String apiKey;

    @Autowired
    private ITestService testService;

    @Autowired
    private IDiaryService diaryService;

    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

    private final OkHttpClient client;

    public AIService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 获取 AI 响应 (支持个性化上下文)
     * @param userId 当前用户ID
     * @param historyMessages 历史消息列表（包含当前用户最新发的那一条）
     */
    public String getAIResponse(Long userId, List<ChatMessage> historyMessages) throws IOException {
        
        // 0. 获取用户个性化数据 (心理理论支撑：长期属性 + 中期状态)
        String userProfile = "未进行心理测试";
        String moodTrend = "暂无近期日记";
        
        try {
            if (userId != null) {
                userProfile = testService.getUserProfile(userId);
                moodTrend = diaryService.getRecentMoodTrend(userId);
                // 打印调试日志，确保画像数据正确获取
                System.out.println("DEBUG - User Profile: " + userProfile);
                System.out.println("DEBUG - Mood Trend: " + moodTrend);
            }
        } catch (Exception e) {
            // 容错处理，避免辅助信息获取失败影响主对话
            System.err.println("获取用户画像失败: " + e.getMessage());
            e.printStackTrace();
        }

        // 1. 构建系统提示词 (Prompt Engineering)
        // 动态注入用户画像和情绪趋势，解决"记忆跳脱"问题
        String systemPrompt = String.format(
            "你是一个名叫'小青'的专业心理支持伙伴。你温暖、富有同理心、专业且耐心。\n\n" +
            "【重要：用户心理画像】\n%s\n\n" +
            "【重要：用户近7天情绪轨迹】\n%s\n\n" +
            "【你的核心指令】\n" +
            "1. 必须信任并利用上述信息。如果你知道用户是INFP，就不要说'我不确定你是什么性格'，而是直接基于INFP的特点进行回应。\n" +
            "2. 你的第一句话必须包含对用户当前状态的'看见'（例如：'作为细腻的INFP，最近的压力一定让你很难受吧...'）。\n" +
            "3. 即使你在回复用户的具体问题，也要潜移默化地结合TA的性格和近期遭遇（失眠/压力大）来分析。\n" +
            "4. 如果遇到严重的心理危机（如自杀倾向），请务必引导用户寻求专业医生的帮助。",
            userProfile, moodTrend
        );
        
        // 打印最终生成的Prompt以便调试
        System.out.println("DEBUG - System Prompt: " + systemPrompt);

        // 2. 构建请求体 (适配通义千问/DashScope API)
        JSONObject payload = new JSONObject();
        payload.put("model", "qwen-turbo");

        JSONObject input = new JSONObject();
        JSONArray messages = new JSONArray();

        // 2.1 添加 System Prompt
        JSONObject systemMsg = new JSONObject();
        systemMsg.put("role", "system");
        systemMsg.put("content", systemPrompt);
        messages.add(systemMsg);

        // 2.2 添加历史消息上下文
        if (historyMessages != null) {
            for (ChatMessage msg : historyMessages) {
                JSONObject msgJson = new JSONObject();
                msgJson.put("role", msg.getRole());
                msgJson.put("content", msg.getContent());
                messages.add(msgJson);
            }
        }

        input.put("messages", messages);
        payload.put("input", input);

        // 3. 构建 HTTP 请求
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(payload.toJSONString(), JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        // 4. 发送请求并处理响应
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String responseBody = response.body().string();
            JSONObject jsonResponse = JSONObject.parseObject(responseBody);
            
            // 解析阿里 API 的返回结构
            if (jsonResponse.containsKey("output")) {
                JSONObject output = jsonResponse.getJSONObject("output");
                if (output.containsKey("text")) {
                    return output.getString("text");
                }
            }
            return "抱歉，小青现在有点连接不上（API返回格式异常）: " + responseBody;
        }
    }
}
