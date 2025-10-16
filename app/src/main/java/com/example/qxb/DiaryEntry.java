package com.example.qxb;

import java.util.List;

public class DiaryEntry {
    private long timestamp;
    private List<String> moods;
    private List<String> reasons;
    private String content;

    public DiaryEntry(long timestamp, List<String> moods, List<String> reasons, String content) {
        this.timestamp = timestamp;
        this.moods = moods;
        this.reasons = reasons;
        this.content = content;
    }

    // Getter methods
    public long getTimestamp() { return timestamp; }
    public List<String> getMoods() { return moods; }
    public List<String> getReasons() { return reasons; }
    public String getContent() { return content; }

    @Override
    public String toString() {
        return "DiaryEntry{" +
                "timestamp=" + timestamp +
                ", moods=" + moods +
                ", reasons=" + reasons +
                ", content='" + content + '\'' +
                '}';
    }
}