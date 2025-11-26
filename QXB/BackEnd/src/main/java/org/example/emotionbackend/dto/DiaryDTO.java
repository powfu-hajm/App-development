package org.example.emotionbackend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class DiaryDTO {
    private Long id;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "日记内容不能为空")
    private String content;

    @NotBlank(message = "心情标签不能为空")
    private String moodTag;

    // 移除createTime字段，由后端自动生成
}