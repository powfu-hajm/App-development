package org.example.emotionbackend.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    public String getAIResponse(String userMessage) throws IOException {
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

        // 添加 System Prompt
        JSONObject systemMsg = new JSONObject();
        systemMsg.put("role", "system");
        systemMsg.put("content", systemPrompt);
        messages.add(systemMsg);

        // 添加 User Message
        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

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
            // 通常结构: { "output": { "text": "..." }, "usage": ... }
            if (jsonResponse.containsKey("output")) {
                JSONObject output = jsonResponse.getJSONObject("output");
                if (output.containsKey("text")) {
                    return output.getString("text");
                }
            }
            // 如果出错或结构不对，返回错误信息以便调试
            return "抱歉，小青现在有点连接不上（API返回格式异常）: " + responseBody;
        }
    }
}


