package org.example.emotionbackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class TestPaperDetailDTO {
    private Long id;
    private String title;
    private String description;
    private Integer questionCount;
    private Integer estimatedTime;
    private String icon;
    private List<TestQuestionDTO> questions;
}
