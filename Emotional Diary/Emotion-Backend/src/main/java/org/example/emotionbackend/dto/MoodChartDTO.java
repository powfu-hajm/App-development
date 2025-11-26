package org.example.emotionbackend.dto;

import lombok.Data;

@Data
public class MoodChartDTO {
    private String date;
    private String mood;
    private Integer count;

    // 添加构造方法以便测试
    public MoodChartDTO() {}

    public MoodChartDTO(String date, String mood, Integer count) {
        this.date = date;
        this.mood = mood;
        this.count = count;
    }

    // 添加数据验证方法
    public boolean isValid() {
        return date != null && !date.trim().isEmpty() &&
                mood != null && !mood.trim().isEmpty() &&
                count != null && count >= 0;
    }
}