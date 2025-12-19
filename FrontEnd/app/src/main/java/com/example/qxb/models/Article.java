package com.example.qxb.models;

public class Article {

    private Long id;           // 文章ID
    private String title;      // 标题
    private String content;    // 内容
    private String author;     // 作者
    private String coverUrl;   // 封面图
    private String publishTime;// 发布时间
    private String summary;
    private int readCount;
    private String source;
    private String originalUrl;

    public Article() {
    }

    public Article(Long id, String title, String content, String author, String coverUrl, String publishTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.coverUrl = coverUrl;
        this.publishTime = publishTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getPublishTime() {
        return publishTime;
    }
    public String getSummary() {
        return summary;
    }

    public int getReadCount() {
        return readCount;
    }

    public String getSource() {
        return source;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }
}
