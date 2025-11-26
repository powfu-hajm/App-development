package com.example.qxb;

public class Article {
    private String id;
    private String title;
    private String summary;
    private String readTime;
    private String category;

    public Article() {}

    public Article(String id, String title, String summary, String readTime, String category) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.readTime = readTime;
        this.category = category;
    }

    // Getter 和 Setter 方法
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getReadTime() { return readTime; }
    public void setReadTime(String readTime) { this.readTime = readTime; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}