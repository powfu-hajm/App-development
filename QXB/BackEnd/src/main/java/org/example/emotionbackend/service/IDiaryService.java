package org.example.emotionbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.emotionbackend.dto.DiaryDTO;
import org.example.emotionbackend.dto.MoodChartDTO;
import org.example.emotionbackend.entity.Diary;

import java.util.List;

public interface IDiaryService extends IService<Diary> {
    List<MoodChartDTO> getMoodChartData(Long userId);

    // 新增：手动更新日记的方法
    boolean updateDiaryWithTime(Diary diary);

    // 新增：使用DTO更新的方法
    boolean updateDiaryWithTime(Diary existingDiary, DiaryDTO diaryDTO);

    // 新增：直接更新实体方法
    boolean updateByIdWithTime(Diary entity);

    // 新增：简化版更新方法
    boolean simpleUpdateDiary(DiaryDTO diaryDTO);

    /**
     * 获取用户最近7天的情绪趋势摘要
     */
    String getRecentMoodTrend(Long userId);
}