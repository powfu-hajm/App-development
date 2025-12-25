package org.example.emotionbackend.controller;

import org.example.emotionbackend.common.Result;
import org.example.emotionbackend.dto.ChatRequest;
import org.example.emotionbackend.dto.ChatResponse;
import org.example.emotionbackend.entity.ChatMessage;
import org.example.emotionbackend.service.AIService;
import org.example.emotionbackend.service.IChatService;
import org.example.emotionbackend.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    @Autowired
    private IChatService chatService;

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return ChatResponse.fail("未登录");
        }

        try {
            // 1. 保存用户消息
            chatService.saveMessage(userId, "user", request.getMessage());

            // 2. 获取最近历史 (比如最近10条，包含刚才保存的那条)
            List<ChatMessage> history = chatService.getRecentMessages(userId, 10);

            // 3. 调用 AI (传入用户ID以便获取画像 + 历史上下文)
            String reply = aiService.getAIResponse(userId, history);

            // 4. 保存 AI 回复
            chatService.saveMessage(userId, "assistant", reply);

            return ChatResponse.success(reply);
        } catch (Exception e) {
            e.printStackTrace();
            return ChatResponse.fail("连接AI服务失败: " + e.getMessage());
        }
    }

    /**
     * 获取聊天历史记录
     */
    @GetMapping("/history")
    public Result<List<ChatMessage>> getHistory() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error("未登录");
        }
        // 获取最近 50 条
        List<ChatMessage> history = chatService.getRecentMessages(userId, 50);
        return Result.success(history);
    }

    @GetMapping("/test")
    public String test() {
        return "Backend is running!";
    }
}
