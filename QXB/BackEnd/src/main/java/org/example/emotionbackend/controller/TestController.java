package org.example.emotionbackend.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.emotionbackend.common.Result;
import org.example.emotionbackend.dto.TestPaperDetailDTO;
import org.example.emotionbackend.dto.TestResultDTO;
import org.example.emotionbackend.dto.TestSubmitDTO;
import org.example.emotionbackend.entity.TestPaper;
import org.example.emotionbackend.service.ITestService;
import org.example.emotionbackend.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ITestService testService;

    /**
     * 获取所有问卷列表
     */
    @GetMapping("/papers")
    public Result<List<TestPaper>> getPapers() {
        try {
            // 获取列表不需要登录
            log.info("获取问卷列表");
            List<TestPaper> papers = testService.getAllPapers();
            return Result.success(papers);
        } catch (Exception e) {
            log.error("获取问卷列表失败", e);
            return Result.error("获取问卷列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取问卷详情（包含所有题目和选项）
     */
    @GetMapping("/paper/{id}")
    public Result<TestPaperDetailDTO> getPaperDetail(@PathVariable("id") Long paperId) {
        try {
            // 查看题目不需要登录
            log.info("获取问卷详情，paperId: {}", paperId);
            TestPaperDetailDTO detail = testService.getPaperDetail(paperId);
            if (detail == null) {
                return Result.error("问卷不存在");
            }
            return Result.success(detail);
        } catch (Exception e) {
            log.error("获取问卷详情失败", e);
            return Result.error("获取问卷详情失败: " + e.getMessage());
        }
    }

    /**
     * 提交测试答案
     */
    @PostMapping("/submit")
    public Result<TestResultDTO> submitTest(@Valid @RequestBody TestSubmitDTO submitDTO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error("未登录");
        }

        try {
            // 强制使用当前用户ID
            submitDTO.setUserId(userId);
            
            log.info("提交测试答案，paperId: {}, userId: {}",
                    submitDTO.getPaperId(), userId);
            TestResultDTO result = testService.submitTest(submitDTO);
            return Result.success("测试完成", result);
        } catch (Exception e) {
            log.error("提交测试答案失败", e);
            return Result.error("提交测试答案失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户测试历史
     */
    @GetMapping("/history")
    public Result<List<TestResultDTO>> getTestHistory(@RequestParam(required = false) Long userId) {
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            return Result.error("未登录");
        }
        
        try {
            log.info("获取用户测试历史，userId: {}", currentUserId);
            List<TestResultDTO> history = testService.getTestHistory(currentUserId);
            return Result.success(history);
        } catch (Exception e) {
            log.error("获取测试历史失败", e);
            return Result.error("获取测试历史失败: " + e.getMessage());
        }
    }
}
