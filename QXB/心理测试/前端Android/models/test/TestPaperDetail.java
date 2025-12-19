package com.example.qxb.models.test;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class TestPaperDetail implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("questionCount")
    private Integer questionCount;

    @SerializedName("estimatedTime")
    private Integer estimatedTime;

    @SerializedName("icon")
    private String icon;

    @SerializedName("questions")
    private List<TestQuestion> questions;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public Integer getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(Integer estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<TestQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<TestQuestion> questions) {
        this.questions = questions;
    }
}
