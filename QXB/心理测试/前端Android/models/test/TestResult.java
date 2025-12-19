package com.example.qxb.models.test;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class TestResult implements Serializable {
    @SerializedName("recordId")
    private Long recordId;

    @SerializedName("paperId")
    private Long paperId;

    @SerializedName("paperTitle")
    private String paperTitle;

    @SerializedName("totalScore")
    private Integer totalScore;

    @SerializedName("resultLevel")
    private String resultLevel;

    @SerializedName("resultTitle")
    private String resultTitle;

    @SerializedName("resultDescription")
    private String resultDescription;

    @SerializedName("suggestion")
    private String suggestion;

    @SerializedName("testTime")
    private String testTime;

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public String getPaperTitle() {
        return paperTitle;
    }

    public void setPaperTitle(String paperTitle) {
        this.paperTitle = paperTitle;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public String getResultLevel() {
        return resultLevel;
    }

    public void setResultLevel(String resultLevel) {
        this.resultLevel = resultLevel;
    }

    public String getResultTitle() {
        return resultTitle;
    }

    public void setResultTitle(String resultTitle) {
        this.resultTitle = resultTitle;
    }

    public String getResultDescription() {
        return resultDescription;
    }

    public void setResultDescription(String resultDescription) {
        this.resultDescription = resultDescription;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getTestTime() {
        return testTime;
    }

    public void setTestTime(String testTime) {
        this.testTime = testTime;
    }
}
