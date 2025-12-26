package org.example.emotionbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.emotionbackend.dto.*;
import org.example.emotionbackend.entity.*;
import org.example.emotionbackend.mapper.*;
import org.example.emotionbackend.service.ITestService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

    @Autowired
    private MbtiTypeMapper mbtiTypeMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // MBTI 问卷的 paperId
    private static final Long MBTI_PAPER_ID = 4L;

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

        // 判断是否为 MBTI 测试
        boolean isMbtiTest = MBTI_PAPER_ID.equals(submitDTO.getPaperId());

        if (isMbtiTest) {
            return submitMbtiTest(submitDTO);
        } else {
            return submitNormalTest(submitDTO);
        }
    }

    /**
     * 提交普通测试（SDS、SAS、PSS等）
     */
    private TestResultDTO submitNormalTest(TestSubmitDTO submitDTO) {
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
        resultDTO.setIsMbtiResult(false);

        return resultDTO;
    }

    /**
     * 提交 MBTI 测试 - 使用四维独立计算算法
     */
    private TestResultDTO submitMbtiTest(TestSubmitDTO submitDTO) {
        log.info("处理MBTI测试，使用四维独立计算算法");
        log.info("收到的 optionIds: {}", submitDTO.getOptionIds());

        // 1. 获取用户选择的所有选项详情（包含维度信息）
        List<TestOption> selectedOptions = optionMapper.selectList(
                new LambdaQueryWrapper<TestOption>()
                        .in(TestOption::getId, submitDTO.getOptionIds())
        );

        log.info("查询到 {} 个选项", selectedOptions.size());
        for (TestOption opt : selectedOptions) {
            log.info("选项ID={}, 内容={}, 维度={}", opt.getId(), opt.getContent(), opt.getDimension());
        }

        // 2. 计算四个维度的得分
        Map<String, Integer> dimensionScores = new HashMap<>();
        dimensionScores.put("E", 0);
        dimensionScores.put("I", 0);
        dimensionScores.put("S", 0);
        dimensionScores.put("N", 0);
        dimensionScores.put("T", 0);
        dimensionScores.put("F", 0);
        dimensionScores.put("J", 0);
        dimensionScores.put("P", 0);

        for (TestOption option : selectedOptions) {
            String dimension = option.getDimension();
            if (dimension != null && dimensionScores.containsKey(dimension)) {
                dimensionScores.put(dimension, dimensionScores.get(dimension) + 1);
            }
        }

        log.info("MBTI维度得分: E={}, I={}, S={}, N={}, T={}, F={}, J={}, P={}",
                dimensionScores.get("E"), dimensionScores.get("I"),
                dimensionScores.get("S"), dimensionScores.get("N"),
                dimensionScores.get("T"), dimensionScores.get("F"),
                dimensionScores.get("J"), dimensionScores.get("P"));

        // 3. 计算百分比并确定类型
        int eiTotal = dimensionScores.get("E") + dimensionScores.get("I");
        int snTotal = dimensionScores.get("S") + dimensionScores.get("N");
        int tfTotal = dimensionScores.get("T") + dimensionScores.get("F");
        int jpTotal = dimensionScores.get("J") + dimensionScores.get("P");

        // 防止除以零
        eiTotal = eiTotal > 0 ? eiTotal : 1;
        snTotal = snTotal > 0 ? snTotal : 1;
        tfTotal = tfTotal > 0 ? tfTotal : 1;
        jpTotal = jpTotal > 0 ? jpTotal : 1;

        int ePercent = (int) Math.round(dimensionScores.get("E") * 100.0 / eiTotal);
        int iPercent = 100 - ePercent;
        int sPercent = (int) Math.round(dimensionScores.get("S") * 100.0 / snTotal);
        int nPercent = 100 - sPercent;
        int tPercent = (int) Math.round(dimensionScores.get("T") * 100.0 / tfTotal);
        int fPercent = 100 - tPercent;
        int jPercent = (int) Math.round(dimensionScores.get("J") * 100.0 / jpTotal);
        int pPercent = 100 - jPercent;

        // 4. 确定 MBTI 类型代码
        StringBuilder mbtiCode = new StringBuilder();
        mbtiCode.append(ePercent >= iPercent ? "E" : "I");
        mbtiCode.append(sPercent >= nPercent ? "S" : "N");
        mbtiCode.append(tPercent >= fPercent ? "T" : "F");
        mbtiCode.append(jPercent >= pPercent ? "J" : "P");
        String mbtiType = mbtiCode.toString();

        log.info("计算得出MBTI类型: {}", mbtiType);
        log.info("维度百分比: E{}%/I{}%, S{}%/N{}%, T{}%/F{}%, J{}%/P{}%",
                ePercent, iPercent, sPercent, nPercent, tPercent, fPercent, jPercent, pPercent);

        // 5. 从数据库获取 MBTI 类型详细信息
        MbtiType mbtiTypeInfo = mbtiTypeMapper.selectByTypeCode(mbtiType);
        if (mbtiTypeInfo == null) {
            log.warn("未找到MBTI类型信息: {}，使用默认数据", mbtiType);
        }

        // 6. 计算总分（用于兼容）
        Integer totalScore = optionMapper.sumScoreByIds(submitDTO.getOptionIds());

        // 7. 保存测试记录
        TestRecord record = new TestRecord();
        record.setUserId(submitDTO.getUserId());
        record.setPaperId(submitDTO.getPaperId());
        record.setTotalScore(totalScore);
        record.setResultLevel(mbtiType); // 用类型代码作为 level
        record.setResultTitle(mbtiTypeInfo != null ?
                mbtiTypeInfo.getTypeName() + " " + mbtiType : mbtiType);
        record.setResultDescription(mbtiTypeInfo != null ?
                mbtiTypeInfo.getDetailDesc() : "");
        record.setSuggestion(mbtiTypeInfo != null ?
                mbtiTypeInfo.getBriefDesc() : "");
        // 保存维度百分比数据
        try {
            Map<String, Integer> percentData = new HashMap<>();
            percentData.put("ePercent", ePercent);
            percentData.put("iPercent", iPercent);
            percentData.put("sPercent", sPercent);
            percentData.put("nPercent", nPercent);
            percentData.put("tPercent", tPercent);
            percentData.put("fPercent", fPercent);
            percentData.put("jPercent", jPercent);
            percentData.put("pPercent", pPercent);
            record.setAnswers(objectMapper.writeValueAsString(percentData));
        } catch (Exception e) {
            log.error("序列化维度百分比失败", e);
        }

        recordMapper.insert(record);
        log.info("MBTI测试记录保存成功，recordId: {}, 类型: {}", record.getId(), mbtiType);

        // 8. 获取问卷标题
        TestPaper paper = paperMapper.selectById(submitDTO.getPaperId());

        // 9. 构建返回结果
        TestResultDTO resultDTO = new TestResultDTO();
        resultDTO.setRecordId(record.getId());
        resultDTO.setPaperId(submitDTO.getPaperId());
        resultDTO.setPaperTitle(paper != null ? paper.getTitle() : "MBTI人格测试");
        resultDTO.setTotalScore(totalScore);
        resultDTO.setTestTime(record.getCreateTime());

        // MBTI 专属字段
        resultDTO.setIsMbtiResult(true);
        resultDTO.setMbtiType(mbtiType);
        resultDTO.setResultLevel(mbtiType);
        resultDTO.setResultTitle(record.getResultTitle());
        resultDTO.setResultDescription(record.getResultDescription());
        resultDTO.setSuggestion(record.getSuggestion());

        // 维度百分比
        resultDTO.setEPercent(ePercent);
        resultDTO.setIPercent(iPercent);
        resultDTO.setSPercent(sPercent);
        resultDTO.setNPercent(nPercent);
        resultDTO.setTPercent(tPercent);
        resultDTO.setFPercent(fPercent);
        resultDTO.setJPercent(jPercent);
        resultDTO.setPPercent(pPercent);

        // 填充 MBTI 类型详细信息
        if (mbtiTypeInfo != null) {
            resultDTO.setTypeName(mbtiTypeInfo.getTypeName());
            resultDTO.setTypeNameEn(mbtiTypeInfo.getTypeNameEn());
            resultDTO.setTypeGroup(mbtiTypeInfo.getTypeGroup());
            resultDTO.setBriefDesc(mbtiTypeInfo.getBriefDesc());
            resultDTO.setDetailDesc(mbtiTypeInfo.getDetailDesc());
            resultDTO.setImageName(mbtiTypeInfo.getImageName());
            resultDTO.setColorPrimary(mbtiTypeInfo.getColorPrimary());
            resultDTO.setColorSecondary(mbtiTypeInfo.getColorSecondary());

            // 解析 JSON 数组字段
            try {
                if (mbtiTypeInfo.getStrengths() != null) {
                    resultDTO.setStrengths(objectMapper.readValue(
                            mbtiTypeInfo.getStrengths(), new TypeReference<List<String>>() {}));
                }
                if (mbtiTypeInfo.getWeaknesses() != null) {
                    resultDTO.setWeaknesses(objectMapper.readValue(
                            mbtiTypeInfo.getWeaknesses(), new TypeReference<List<String>>() {}));
                }
                if (mbtiTypeInfo.getCareerSuggestions() != null) {
                    resultDTO.setCareerSuggestions(objectMapper.readValue(
                            mbtiTypeInfo.getCareerSuggestions(), new TypeReference<List<String>>() {}));
                }
                if (mbtiTypeInfo.getFamousPeople() != null) {
                    resultDTO.setFamousPeople(objectMapper.readValue(
                            mbtiTypeInfo.getFamousPeople(), new TypeReference<List<String>>() {}));
                }
            } catch (Exception e) {
                log.error("解析MBTI类型JSON字段失败", e);
            }
        }

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
