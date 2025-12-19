package com.example.qxb;

public class Article {
    private Long id;
    private String title;
    private String summary;
    private String coverUrl;
    private String originalUrl;
    private String source;
    private Integer readCount;
    
    // 兼容旧字段
    private String readTime;
    private String category;

    public Article() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public String getOriginalUrl() { return originalUrl; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Integer getReadCount() { return readCount; }
    public void setReadCount(Integer readCount) { this.readCount = readCount; }

    // 兼容逻辑
    public String getReadTime() {
        if (readCount != null) {
            return readCount + "阅读";
        }
        return readTime != null ? readTime : "3分钟阅读";
    }
    public void setReadTime(String readTime) { this.readTime = readTime; }

    public String getCategory() {
        return source != null ? source : (category != null ? category : "心理科普");
    }
    public void setCategory(String category) { this.category = category; }
}
