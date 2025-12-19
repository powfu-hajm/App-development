package org.example.emotionbackend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TestResultDTO {
    private Long recordId;
    private Long paperId;
    private String paperTitle;
    private Integer totalScore;
    private String resultLevel;
    private String resultTitle;
    private String resultDescription;
    private String suggestion;
    private LocalDateTime testTime;
}
