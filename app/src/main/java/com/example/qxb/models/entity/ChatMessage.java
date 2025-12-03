package com.example.qxb.models.entity;

import java.util.Date;

/**
 * 聊天消息实体类
 * 用于存储单条聊天消息的数据模型
 */
public class ChatMessage {
    private Long id;
    private String content;
    private MessageType type;
    private String senderId;
    private String senderName;
    private Date timestamp;
    private boolean isRead;
    private String conversationId;

    public enum MessageType {
        TEXT,       // 文本消息
        IMAGE,      // 图片
        VOICE,      // 语音
        SYSTEM      // 系统消息
    }

    // 默认构造函数
    public ChatMessage() {
        this.timestamp = new Date();
        this.isRead = false;
    }

    // 简化构造函数
    public ChatMessage(String content, MessageType type, String senderId) {
        this();
        this.content = content;
        this.type = type;
        this.senderId = senderId;
    }

    // 完整构造函数
    public ChatMessage(String content, MessageType type, String senderId,
                       String senderName, String conversationId) {
        this(content, type, senderId);
        this.senderName = senderName;
        this.conversationId = conversationId;
    }

    // Getter 和 Setter 方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    // 业务逻辑方法
    public boolean isUserMessage() {
        return MessageType.TEXT.equals(type) || MessageType.IMAGE.equals(type);
    }

    public boolean isSystemMessage() {
        return MessageType.SYSTEM.equals(type);
    }

    public String getDisplayTime() {
        // 简化的时间格式化，实际项目中可以使用SimpleDateFormat
        return String.format("%tH:%tM", timestamp, timestamp);
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", type=" + type +
                ", senderId='" + senderId + '\'' +
                ", senderName='" + senderName + '\'' +
                ", timestamp=" + timestamp +
                ", isRead=" + isRead +
                ", conversationId='" + conversationId + '\'' +
                '}';
    }
}