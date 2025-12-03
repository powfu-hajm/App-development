package com.qxb.backend.controller;

import com.qxb.backend.dto.ChatRequest;
import com.qxb.backend.dto.ChatResponse;
import com.qxb.backend.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        try {
            String reply = aiService.getAIResponse(request.getMessage());
            return ChatResponse.success(reply);
        } catch (Exception e) {
            e.printStackTrace();
            return ChatResponse.fail("连接AI服务失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/test")
    public String test() {
        return "Backend is running!";
    }
}


