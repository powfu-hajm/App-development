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

        if (messages.isEmpty()) {
            return messages;
        }

        // --- 核心优化：Session 会话切分 ---
        // 目标：如果发现某两条消息之间间隔超过 30 分钟，说明是上一次的对话，
        // 应该截断前面的历史，只保留最近这一次"连续对话"的内容。
        // 防止 AI 被很久以前的对话内容（如早上的负面情绪）误导。
        
        // 阈值：30分钟 (可调整)
        long SESSION_TIMEOUT_MINUTES = 30;
        
        int cutIndex = messages.size(); // 默认保留所有（即截断点在最末尾的后面）

        // 从最新的消息开始往回看 (messages[0] 是最新的, messages[1] 是次新的...)
        // 注意：mybatis查询出来是倒序的，index 0 是最新的
        for (int i = 0; i < messages.size() - 1; i++) {
            LocalDateTime currentMsgTime = messages.get(i).getCreateTime();
            LocalDateTime prevMsgTime = messages.get(i + 1).getCreateTime(); // 前一条（更旧的）

            // 计算时间差（分钟）
            // 注意：LocalDateTime 没有直接的 duration 方法，需要手动计算
            java.time.Duration duration = java.time.Duration.between(prevMsgTime, currentMsgTime);
            long diffMinutes = Math.abs(duration.toMinutes());

            if (diffMinutes > SESSION_TIMEOUT_MINUTES) {
                // 发现断层！
                // i 是当前会话最早的一条。 i+1 是上一次会话的最后一条。
                // 所以我们需要保留 0 到 i 的元素。
                cutIndex = i + 1;
                break;
            }
        }

        // 截取当前会话的消息
        List<ChatMessage> currentSessionMessages = messages.subList(0, cutIndex);

        // 因为大模型需要按时间正序（旧->新），所以这里要反转一下
        Collections.reverse(currentSessionMessages);
        return currentSessionMessages;
    }
}

