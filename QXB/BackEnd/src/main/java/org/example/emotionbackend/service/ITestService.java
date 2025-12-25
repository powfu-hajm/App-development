package org.example.emotionbackend.service;

import org.example.emotionbackend.dto.TestPaperDetailDTO;
import org.example.emotionbackend.dto.TestResultDTO;
import org.example.emotionbackend.dto.TestSubmitDTO;
import org.example.emotionbackend.entity.TestPaper;

import java.util.List;

public interface ITestService {

    /**
     * 获取所有问卷列表
     */
    List<TestPaper> getAllPapers();

    /**
     * 获取问卷详情（包含所有题目和选项）
     */
    TestPaperDetailDTO getPaperDetail(Long paperId);

    /**
     * 提交测试答案并计算结果
     */
    TestResultDTO submitTest(TestSubmitDTO submitDTO);

    /**
     * 获取用户的测试历史记录
     */
    List<TestResultDTO> getTestHistory(Long userId);

    /**
     * 获取用户最新的各项测试结果（如MBTI、SDS等）
     */
    String getUserProfile(Long userId);
}
