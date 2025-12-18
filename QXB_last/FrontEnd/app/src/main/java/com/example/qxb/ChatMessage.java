package com.example.qxb;


public class ChatMessage {
    public static final int TYPE_USER = 0;
    public static final int TYPE_AI = 1;
    public static final int TYPE_TYPING = 2; // 新增：正在输入状态

    private String content;
    private int type;
    private long timestamp;

    public ChatMessage(String content, int type, long timestamp) {
        this.content = content;
        this.type = type;
        this.timestamp = timestamp;
    }

    // Getter and Setter methods
    public String getContent() { return content; }
    public int getType() { return type; }
    public long getTimestamp() { return timestamp; }
}