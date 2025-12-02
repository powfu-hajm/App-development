package org.example.emotionbackend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class TestSubmitDTO {

    @NotNull(message = "问卷ID不能为空")
    private Long paperId;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotEmpty(message = "答案不能为空")
    private List<Long> optionIds;
}
