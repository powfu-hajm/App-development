package com.example.qxb.models.network;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Diary implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("content")
    private String content;

    @SerializedName("moodTag")
    private String moodTag;

    @SerializedName("createTime")
    private String createTime;  // 保持为String类型

    @SerializedName("updateTime")
    private String updateTime;  // 保持为String类型

    // 必须有无参构造函数
    public Diary() {}

    public Diary(Long userId, String content, String moodTag) {
        this.userId = userId;
        this.content = content;
        this.moodTag = moodTag;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getMoodTag() { return moodTag; }
    public void setMoodTag(String moodTag) { this.moodTag = moodTag; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }

    public String getUpdateTime() { return updateTime; }
    public void setUpdateTime(String updateTime) { this.updateTime = updateTime; }

    @Override
    public String toString() {
        return "Diary{" +
                "id=" + id +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                ", moodTag='" + moodTag + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}