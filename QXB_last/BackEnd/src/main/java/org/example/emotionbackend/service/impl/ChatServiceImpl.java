package org.example.emotionbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.emotionbackend.entity.ChatMessage;
import org.example.emotionbackend.mapper.ChatMessageMapper;
import org.example.emotionbackend.service.IChatService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class ChatServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements IChatService {

    @Override
    public void saveMessage(Long userId, String role, String content) {
        ChatMessage message = new ChatMessage();
        message.setUserId(userId);
        message.setRole(role);
        message.setContent(content);
        message.setCreateTime(LocalDateTime.now());
        this.save(message);
    }

    @Override
    public List<ChatMessage> getRecentMessages(Long userId, int limit) {
        // 先按时间倒序查出最近的N条
        List<ChatMessage> messages = this.lambdaQuery()
                .eq(ChatMessage::getUserId, userId)
                .orderByDesc(ChatMessage::getCreateTime)
                .last("LIMIT " + limit)
                .list();

        // 因为大模型需要按时间正序（旧->新），所以这里要反转一下
        Collections.reverse(messages);
        return messages;
    }
}

