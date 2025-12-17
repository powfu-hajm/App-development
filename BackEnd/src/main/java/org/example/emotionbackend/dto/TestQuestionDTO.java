package org.example.emotionbackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class TestQuestionDTO {
    private Long id;
    private Integer questionOrder;
    private String content;
    private List<TestOptionDTO> options;
}
