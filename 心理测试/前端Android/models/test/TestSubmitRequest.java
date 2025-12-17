package com.example.qxb.models.test;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TestSubmitRequest {
    @SerializedName("paperId")
    private Long paperId;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("optionIds")
    private List<Long> optionIds;

    public TestSubmitRequest(Long paperId, Long userId, List<Long> optionIds) {
        this.paperId = paperId;
        this.userId = userId;
        this.optionIds = optionIds;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Long> getOptionIds() {
        return optionIds;
    }

    public void setOptionIds(List<Long> optionIds) {
        this.optionIds = optionIds;
    }
}
