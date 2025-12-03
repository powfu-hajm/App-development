package com.example.qxb.models.network;

import com.example.qxb.models.entity.ChatMessage;
import java.util.List;

public class ChatResponse {
    private boolean success;
    private String message;
    private ChatMessage data;
    private List<ChatMessage> messages;
    private int totalCount;

    // 构造函数
    public ChatResponse() {}

    public ChatResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Getter 和 Setter
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public ChatMessage getData() { return data; }
    public void setData(ChatMessage data) { this.data = data; }

    public List<ChatMessage> getMessages() { return messages; }
    public void setMessages(List<ChatMessage> messages) { this.messages = messages; }

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
}