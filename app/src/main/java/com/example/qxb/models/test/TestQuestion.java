package com.example.qxb.models.test;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class TestQuestion implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("questionOrder")
    private Integer questionOrder;

    @SerializedName("content")
    private String content;

    @SerializedName("options")
    private List<TestOption> options;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<TestOption> getOptions() {
        return options;
    }

    public void setOptions(List<TestOption> options) {
        this.options = options;
    }
}
