package org.example.emotionbackend.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import okhttp3.*;
import org.example.emotionbackend.entity.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AIService {

    @Value("${ai.api.key}")
    private String apiKey;

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
     * 获取 AI 响应 (支持上下文)
     * @param historyMessages 历史消息列表（包含当前用户最新发的那一条）
     */
    public String getAIResponse(List<ChatMessage> historyMessages) throws IOException {
        // 1. 构建系统提示词 (Prompt Engineering)
        String systemPrompt = "你是一个名叫'小青'的心理支持伙伴。你温暖、富有同理心、专业且耐心。" +
                "当用户表达负面情绪时，请先接纳他们的感受，再提供适当的建议。" +
                "请不要仅仅给出冷冰冰的医学建议，而是像朋友一样交谈。" +
                "如果遇到严重的心理危机（如自杀倾向），请务必引导用户寻求专业医生的帮助。";

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
                // 转换角色名: 数据库里的 role 可能是 "user"/"assistant"，直接用即可
                // 如果数据库存的是其他格式，这里需要转换
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
