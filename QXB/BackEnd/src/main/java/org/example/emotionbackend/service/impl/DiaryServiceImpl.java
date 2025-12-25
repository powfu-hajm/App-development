package org.example.emotionbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.emotionbackend.dto.DiaryDTO;
import org.example.emotionbackend.dto.MoodChartDTO;
import org.example.emotionbackend.entity.Diary;
import org.example.emotionbackend.mapper.DiaryMapper;
import org.example.emotionbackend.service.IDiaryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class DiaryServiceImpl extends ServiceImpl<DiaryMapper, Diary> implements IDiaryService {

    // 日期格式化工具方法
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MoodChartDTO> getMoodChartData(Long userId) {
        try {
            log.info("开始获取用户 {} 的情绪图表数据", userId);
            List<MoodChartDTO> result = baseMapper.selectMoodChartData(userId);
            log.info("成功获取用户 {} 的情绪图表数据，共 {} 条记录", userId, result.size());
            return result;
        } catch (Exception e) {
            log.error("获取情绪图表数据失败，用户ID: {}", userId, e);
            throw new RuntimeException("获取情绪图表数据失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDiaryWithTime(Diary diary) {
        try {
            log.info("开始更新日记 - ID: {}, 用户ID: {}", diary.getId(), diary.getUserId());

            // 使用自定义SQL更新，确保更新时间被设置
            int result = baseMapper.updateDiaryWithTime(
                    diary.getId(),
                    diary.getUserId(),
                    diary.getContent(),
                    diary.getMoodTag()
            );

            if (result > 0) {
                log.info("自定义SQL更新成功，影响行数: {}", result);
                return true;
            }

            log.warn("自定义SQL更新失败，尝试其他方法");
            return false;
        } catch (Exception e) {
            log.error("更新日记失败", e);
            throw new RuntimeException("更新日记失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDiaryWithTime(Diary existingDiary, DiaryDTO diaryDTO) {
        try {
            log.info("开始更新日记，ID: {}, 用户ID: {}", diaryDTO.getId(), diaryDTO.getUserId());

            // 方法1：使用自定义SQL更新（最可靠）
            log.info("尝试方法1：自定义SQL更新");
            int sqlResult = baseMapper.updateDiaryWithTime(
                    diaryDTO.getId(),
                    diaryDTO.getUserId(),
                    diaryDTO.getContent(),
                    diaryDTO.getMoodTag()
            );

            if (sqlResult > 0) {
                log.info("方法1成功 - SQL更新成功，影响行数: {}", sqlResult);
                return true;
            }

            // 方法2：直接更新实体
            log.info("尝试方法2：直接更新实体");
            Diary updateEntity = new Diary();
            updateEntity.setId(diaryDTO.getId());
            updateEntity.setUserId(diaryDTO.getUserId());
            updateEntity.setContent(diaryDTO.getContent());
            updateEntity.setMoodTag(diaryDTO.getMoodTag());
            // 保留原创建时间
            updateEntity.setCreateTime(existingDiary.getCreateTime());
            // 强制设置更新时间
            updateEntity.setUpdateTime(LocalDateTime.now());

            boolean entityResult = this.updateById(updateEntity);
            if (entityResult) {
                log.info("方法2成功 - 实体更新成功");
                return true;
            }

            // 方法3：使用lambdaUpdate
            log.info("尝试方法3：lambdaUpdate");
            boolean lambdaResult = this.lambdaUpdate()
                    .eq(Diary::getId, diaryDTO.getId())
                    .eq(Diary::getUserId, diaryDTO.getUserId())
                    .set(Diary::getContent, diaryDTO.getContent())
                    .set(Diary::getMoodTag, diaryDTO.getMoodTag())
                    .set(Diary::getUpdateTime, LocalDateTime.now())
                    .update();

            if (lambdaResult) {
                log.info("方法3成功 - lambdaUpdate成功");
            } else {
                log.error("所有更新方法都失败了");
            }

            return lambdaResult;

        } catch (Exception e) {
            log.error("更新日记时发生异常", e);
            throw new RuntimeException("更新日记失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateByIdWithTime(Diary entity) {
        if (entity == null || entity.getId() == null) {
            log.warn("更新实体为空或ID为空");
            return false;
        }

        log.info("直接更新日记实体，ID: {}", entity.getId());

        // 手动设置更新时间
        entity.setUpdateTime(LocalDateTime.now());

        boolean result = this.updateById(entity);
        log.info("直接更新结果: {}", result ? "成功" : "失败");
        return result;
    }

    // 简化版的更新方法
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean simpleUpdateDiary(DiaryDTO diaryDTO) {
        try {
            log.info("简单更新日记，ID: {}, 用户ID: {}", diaryDTO.getId(), diaryDTO.getUserId());

            // 直接使用SQL更新，确保时间正确
            int result = baseMapper.updateDiaryWithTime(
                    diaryDTO.getId(),
                    diaryDTO.getUserId(),
                    diaryDTO.getContent(),
                    diaryDTO.getMoodTag()
            );

            if (result > 0) {
                log.info("简单更新成功，影响行数: {}", result);
                return true;
            } else {
                log.error("简单更新失败，影响行数: {}", result);
                return false;
            }
        } catch (Exception e) {
            log.error("简单更新日记失败", e);
            throw new RuntimeException("更新日记失败", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String getRecentMoodTrend(Long userId) {
        log.info("分析用户 {} 最近7天情绪趋势", userId);

        // 获取最近7天的日记
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Diary> recentDiaries = this.lambdaQuery()
                .eq(Diary::getUserId, userId)
                .ge(Diary::getCreateTime, sevenDaysAgo)
                .orderByDesc(Diary::getCreateTime)
                .list();

        if (recentDiaries == null || recentDiaries.isEmpty()) {
            return "最近7天暂无日记记录";
        }

        // 1. 计算平均情绪值
        // 假设 MoodTag 是字符串，需要映射为数值。如果MoodTag已经是数值(1-5)，则直接使用。
        // 根据现有逻辑 MoodTag 通常是 "开心", "难过" 等或数字。这里假设是数字字符串 "1"-"5"
        // 5=非常开心, 4=开心, 3=一般, 2=难过, 1=非常难过
        
        double totalScore = 0;
        int count = 0;
        StringBuilder keywords = new StringBuilder();

        for (Diary diary : recentDiaries) {
            String mood = diary.getMoodTag();
            if (mood != null) {
                try {
                    // 尝试解析数字
                    int score = Integer.parseInt(mood);
                    totalScore += score;
                    count++;
                } catch (NumberFormatException e) {
                    // 如果是文字标签，进行简单映射
                    if (mood.contains("开心") || mood.contains("快乐")) totalScore += 4;
                    else if (mood.contains("难过") || mood.contains("伤心")) totalScore += 2;
                    else if (mood.contains("愤怒") || mood.contains("生气")) totalScore += 1;
                    else totalScore += 3; // 默认一般
                    count++;
                }
            }
            
            // 简单提取日记前20个字作为摘要
            if (diary.getContent() != null && !diary.getContent().isEmpty()) {
                String content = diary.getContent();
                keywords.append(content.length() > 20 ? content.substring(0, 20) : content).append("...; ");
            }
        }

        double avgScore = count > 0 ? totalScore / count : 3.0;
        String moodDesc;
        if (avgScore >= 4) moodDesc = "积极乐观";
        else if (avgScore >= 3) moodDesc = "平稳";
        else moodDesc = "低落/消极";

        return String.format("最近7天共记录%d篇日记，整体情绪指数 %.1f/5.0 (%s)。\n部分日记摘要: %s",
                recentDiaries.size(), avgScore, moodDesc, keywords.toString());
    }
}