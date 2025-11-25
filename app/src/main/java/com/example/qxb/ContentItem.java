package com.example.qxb;

import android.os.Parcel;
import android.os.Parcelable;

public class ContentItem implements Parcelable {
    private String id;
    private String title;
    private String description;
    private String type;
    private String mediaUrl;
    private String readTime;  // 新增：阅读时间
    private String category;  // 新增：分类

    // 空构造函数
    public ContentItem() {}

    // 基础构造函数
    public ContentItem(String id, String title, String description, String type) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
    }

    // 全参构造函数
    public ContentItem(String id, String title, String description, String type,
                       String readTime, String category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.readTime = readTime;
        this.category = category;
    }

    // Parcelable 构造函数
    protected ContentItem(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        type = in.readString();
        mediaUrl = in.readString();
        readTime = in.readString();  // 新增
        category = in.readString();   // 新增
    }

    // Parcelable 实现
    public static final Creator<ContentItem> CREATOR = new Creator<ContentItem>() {
        @Override
        public ContentItem createFromParcel(Parcel in) {
            return new ContentItem(in);
        }

        @Override
        public ContentItem[] newArray(int size) {
            return new ContentItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(type);
        dest.writeString(mediaUrl);
        dest.writeString(readTime);  // 新增
        dest.writeString(category);   // 新增
    }

    // ========== Getter 和 Setter 方法 ==========

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }

    // 新增：阅读时间的 Getter 和 Setter
    public String getReadTime() { return readTime; }
    public void setReadTime(String readTime) { this.readTime = readTime; }

    // 新增：分类的 Getter 和 Setter
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    // 重写 toString 方法，便于调试
    @Override
    public String toString() {
        return "ContentItem{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", readTime='" + readTime + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}