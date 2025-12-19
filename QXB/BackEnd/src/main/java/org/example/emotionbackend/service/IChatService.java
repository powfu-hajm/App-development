package org.example.emotionbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.emotionbackend.entity.ChatMessage;

import java.util.List;

public interface IChatService extends IService<ChatMessage> {
    
    /**
     * 保存一条消息
     */
    void saveMessage(Long userId, String role, String content);

    /**
     * 获取用户最近的N条聊天记录（用于构建上下文）
     * @param limit 条数限制
     */
    List<ChatMessage> getRecentMessages(Long userId, int limit);
}

