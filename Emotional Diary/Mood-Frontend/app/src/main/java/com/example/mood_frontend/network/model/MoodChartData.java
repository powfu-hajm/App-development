package com.example.mood_frontend.network.model;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MoodChartData {
    @SerializedName("date")
    private String date;

    @SerializedName("mood")
    private String mood;

    @SerializedName("count")
    private Integer count;

    // 必须有无参构造函数
    public MoodChartData() {}

    public MoodChartData(String date, String mood, Integer count) {
        this.date = date;
        this.mood = mood;
        this.count = count;
    }

    // Getters and Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public Integer getCount() {
        return count != null ? count : 0;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    // 修复：格式化日期显示 - 用于折线图横轴
    public String getFormattedDate() {
        if (date == null || date.isEmpty()) {
            return "";
        }
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
            Date dateObj = inputFormat.parse(date);
            return outputFormat.format(dateObj);
        } catch (Exception e) {
            // 如果解析失败，尝试其他格式
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
                Date dateObj = inputFormat.parse(date);
                return outputFormat.format(dateObj);
            } catch (Exception e2) {
                // 最后手段：提取日期部分
                if (date.length() >= 10) {
                    return date.substring(5, 10).replace('-', '/');
                }
                return date.length() >= 5 ? date.substring(5) : date;
            }
        }
    }

    // 新增：完整日期格式
    public String getFullFormattedDate() {
        if (date == null || date.isEmpty()) {
            return "";
        }
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
            Date dateObj = inputFormat.parse(date);
            return outputFormat.format(dateObj);
        } catch (Exception e) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
                Date dateObj = inputFormat.parse(date);
                return outputFormat.format(dateObj);
            } catch (Exception e2) {
                return date;
            }
        }
    }

    @Override
    public String toString() {
        return "MoodChartData{" +
                "date='" + date + '\'' +
                ", mood='" + mood + '\'' +
                ", count=" + count +
                '}';
    }

    // 新增：数据验证方法
    public boolean isValid() {
        return date != null && !date.trim().isEmpty() &&
                mood != null && !mood.trim().isEmpty() &&
                count != null && count >= 0;
    }
}