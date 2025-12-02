package org.example.emotionbackend.dto;

import lombok.Data;

@Data
public class TestOptionDTO {
    private Long id;
    private Integer optionOrder;
    private String content;
    // 注意：不返回score，防止前端作弊
}
