package org.example.emotionbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.emotionbackend.dto.*;
import org.example.emotionbackend.entity.*;
import org.example.emotionbackend.mapper.*;
import org.example.emotionbackend.service.ITestService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TestServiceImpl implements ITestService {

    @Autowired
    private TestPaperMapper paperMapper;

    @Autowired
    private TestQuestionMapper questionMapper;

    @Autowired
    private TestOptionMapper optionMapper;

    @Autowired
    private TestResultRangeMapper resultRangeMapper;

    @Autowired
    private TestRecordMapper recordMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TestPaper> getAllPapers() {
        log.info("获取所有问卷列表");
        return paperMapper.selectList(
                new LambdaQueryWrapper<TestPaper>()
                        .orderByAsc(TestPaper::getId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TestPaperDetailDTO getPaperDetail(Long paperId) {
        log.info("获取问卷详情，paperId: {}", paperId);

        // 1. 获取问卷基本信息
        TestPaper paper = paperMapper.selectById(paperId);
        if (paper == null) {
            log.error("问卷不存在，paperId: {}", paperId);
            return null;
        }

        // 2. 获取所有题目
        List<TestQuestion> questions = questionMapper.selectList(
                new LambdaQueryWrapper<TestQuestion>()
                        .eq(TestQuestion::getPaperId, paperId)
                        .orderByAsc(TestQuestion::getQuestionOrder)
        );

        // 3. 获取所有选项
        List<Long> questionIds = questions.stream()
                .map(TestQuestion::getId)
                .collect(Collectors.toList());

        List<TestOption> allOptions = new ArrayList<>();
        if (!questionIds.isEmpty()) {
            allOptions = optionMapper.selectList(
                    new LambdaQueryWrapper<TestOption>()
                            .in(TestOption::getQuestionId, questionIds)
                            .orderByAsc(TestOption::getOptionOrder)
            );
        }

        // 4. 组装DTO
        TestPaperDetailDTO detailDTO = new TestPaperDetailDTO();
        BeanUtils.copyProperties(paper, detailDTO);

        List<TestQuestionDTO> questionDTOs = new ArrayList<>();
        for (TestQuestion question : questions) {
            TestQuestionDTO questionDTO = new TestQuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);

            // 过滤该题目的选项
            List<TestOptionDTO> optionDTOs = allOptions.stream()
                    .filter(opt -> opt.getQuestionId().equals(question.getId()))
                    .map(opt -> {
                        TestOptionDTO optionDTO = new TestOptionDTO();
                        optionDTO.setId(opt.getId());
                        optionDTO.setOptionOrder(opt.getOptionOrder());
                        optionDTO.setContent(opt.getContent());
                        // 不设置score，防止作弊
                        return optionDTO;
                    })
                    .collect(Collectors.toList());

            questionDTO.setOptions(optionDTOs);
            questionDTOs.add(questionDTO);
        }

        detailDTO.setQuestions(questionDTOs);
        log.info("问卷详情获取成功，共 {} 道题目", questionDTOs.size());
        return detailDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TestResultDTO submitTest(TestSubmitDTO submitDTO) {
        log.info("提交测试答案，paperId: {}, userId: {}, 答案数量: {}",
                submitDTO.getPaperId(), submitDTO.getUserId(), submitDTO.getOptionIds().size());

        // 1. 计算总分
        Integer totalScore = optionMapper.sumScoreByIds(submitDTO.getOptionIds());
        log.info("计算得分: {}", totalScore);

        // 2. 根据总分查找对应的结果区间
        TestResultRange resultRange = resultRangeMapper.selectOne(
                new LambdaQueryWrapper<TestResultRange>()
                        .eq(TestResultRange::getPaperId, submitDTO.getPaperId())
                        .le(TestResultRange::getMinScore, totalScore)
                        .ge(TestResultRange::getMaxScore, totalScore)
        );

        if (resultRange == null) {
            log.error("未找到对应的结果区间，paperId: {}, totalScore: {}",
                    submitDTO.getPaperId(), totalScore);
            throw new RuntimeException("未找到对应的测试结果");
        }

        // 3. 保存测试记录
        TestRecord record = new TestRecord();
        record.setUserId(submitDTO.getUserId());
        record.setPaperId(submitDTO.getPaperId());
        record.setTotalScore(totalScore);
        record.setResultLevel(resultRange.getResultLevel());
        record.setResultTitle(resultRange.getResultTitle());
        record.setResultDescription(resultRange.getResultDescription());
        record.setSuggestion(resultRange.getSuggestion());
        // 可选：保存用户答案JSON
        // record.setAnswers(JSON.toJSONString(submitDTO.getOptionIds()));

        recordMapper.insert(record);
        log.info("测试记录保存成功，recordId: {}", record.getId());

        // 4. 获取问卷标题
        TestPaper paper = paperMapper.selectById(submitDTO.getPaperId());

        // 5. 构建返回结果
        TestResultDTO resultDTO = new TestResultDTO();
        resultDTO.setRecordId(record.getId());
        resultDTO.setPaperId(submitDTO.getPaperId());
        resultDTO.setPaperTitle(paper != null ? paper.getTitle() : "");
        resultDTO.setTotalScore(totalScore);
        resultDTO.setResultLevel(resultRange.getResultLevel());
        resultDTO.setResultTitle(resultRange.getResultTitle());
        resultDTO.setResultDescription(resultRange.getResultDescription());
        resultDTO.setSuggestion(resultRange.getSuggestion());
        resultDTO.setTestTime(record.getCreateTime());

        return resultDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestResultDTO> getTestHistory(Long userId) {
        log.info("获取用户测试历史，userId: {}", userId);

        List<TestRecord> records = recordMapper.selectList(
                new LambdaQueryWrapper<TestRecord>()
                        .eq(TestRecord::getUserId, userId)
                        .orderByDesc(TestRecord::getCreateTime)
        );

        return records.stream().map(record -> {
            TestResultDTO dto = new TestResultDTO();
            dto.setRecordId(record.getId());
            dto.setPaperId(record.getPaperId());
            dto.setTotalScore(record.getTotalScore());
            dto.setResultLevel(record.getResultLevel());
            dto.setResultTitle(record.getResultTitle());
            dto.setResultDescription(record.getResultDescription());
            dto.setSuggestion(record.getSuggestion());
            dto.setTestTime(record.getCreateTime());

            // 获取问卷标题
            TestPaper paper = paperMapper.selectById(record.getPaperId());
            dto.setPaperTitle(paper != null ? paper.getTitle() : "");

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public String getUserProfile(Long userId) {
        log.info("构建用户测试画像，userId: {}", userId);
        StringBuilder profile = new StringBuilder();

        // 获取用户最近的测试记录
        List<TestRecord> records = recordMapper.selectList(
                new LambdaQueryWrapper<TestRecord>()
                        .eq(TestRecord::getUserId, userId)
                        .orderByDesc(TestRecord::getCreateTime)
        );

        if (records.isEmpty()) {
            return "暂无心理测试记录";
        }

        // 整理最新的各项测试结果
        // 假设 paperId: 1=SDS(抑郁), 2=SAS(焦虑), 3=PSS(压力), 4=MBTI
        // 这里需要根据实际数据库中的ID来匹配，或者根据PaperTitle匹配
        
        TestRecord mbtiRecord = null;
        TestRecord sdsRecord = null;
        TestRecord sasRecord = null;
        TestRecord pssRecord = null;

        for (TestRecord record : records) {
            if (mbtiRecord == null && record.getPaperId() == 4) mbtiRecord = record;
            if (sdsRecord == null && record.getPaperId() == 1) sdsRecord = record;
            if (sasRecord == null && record.getPaperId() == 2) sasRecord = record;
            if (pssRecord == null && record.getPaperId() == 3) pssRecord = record;
        }

        if (mbtiRecord != null) {
            profile.append("【人格类型(MBTI)】: ").append(mbtiRecord.getResultTitle()).append("\n");
        }
        if (sdsRecord != null) {
            profile.append("【抑郁倾向】: ").append(sdsRecord.getResultTitle()).append("\n");
        }
        if (sasRecord != null) {
            profile.append("【焦虑倾向】: ").append(sasRecord.getResultTitle()).append("\n");
        }
        if (pssRecord != null) {
            profile.append("【压力状况】: ").append(pssRecord.getResultTitle()).append("\n");
        }
        
        if (profile.length() == 0) {
            return "用户做过测试，但暂无主要指标数据";
        }

        return profile.toString();
    }
}
