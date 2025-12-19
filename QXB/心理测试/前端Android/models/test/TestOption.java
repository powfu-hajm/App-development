package com.example.qxb.models.test;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class TestOption implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("optionOrder")
    private Integer optionOrder;

    @SerializedName("content")
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOptionOrder() {
        return optionOrder;
    }

    public void setOptionOrder(Integer optionOrder) {
        this.optionOrder = optionOrder;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOptionLabel() {
        if (optionOrder == null) return "";
        switch (optionOrder) {
            case 1: return "A";
            case 2: return "B";
            case 3: return "C";
            case 4: return "D";
            default: return String.valueOf(optionOrder);
        }
    }
}
