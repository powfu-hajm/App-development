package org.example.emotionbackend.controller;

import org.example.emotionbackend.common.Result;
import org.example.emotionbackend.dto.DiaryDTO;
import org.example.emotionbackend.dto.MoodChartDTO;
import org.example.emotionbackend.entity.Diary;
import org.example.emotionbackend.service.IDiaryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class DiaryController {

    @Autowired
    private IDiaryService diaryService;

    @PostMapping("/diary")
    public Result<Diary> createDiary(@Valid @RequestBody DiaryDTO diaryDTO) {
        try {
            Diary diary = new Diary();
            BeanUtils.copyProperties(diaryDTO, diary);

            log.info("创建日记 - 用户ID: {}, 内容: {}, 心情: {}",
                    diary.getUserId(), diary.getContent(), diary.getMoodTag());

            boolean saved = diaryService.save(diary);
            if (saved) {
                log.info("日记保存成功，ID: {}", diary.getId());
                Diary savedDiary = diaryService.getById(diary.getId());
                log.info("保存后的日记数据 - ID: {}, 创建时间: {}, 更新时间: {}",
                        savedDiary.getId(), savedDiary.getCreateTime(), savedDiary.getUpdateTime());
                return Result.success("日记创建成功", savedDiary);
            } else {
                log.error("日记保存失败");
                return Result.error("日记保存失败");
            }
        } catch (Exception e) {
            log.error("创建日记失败", e);
            return Result.error("创建日记失败: " + e.getMessage());
        }
    }

    @GetMapping("/diaries")
    public Result<List<Diary>> getDiaries(@RequestParam Long userId) {
        try {
            List<Diary> diaries = diaryService.lambdaQuery()
                    .eq(Diary::getUserId, userId)
                    .orderByDesc(Diary::getCreateTime)
                    .list();

            log.info("获取用户 {} 的日记列表，共 {} 条记录", userId, diaries.size());
            return Result.success(diaries);
        } catch (Exception e) {
            log.error("获取日记列表失败", e);
            return Result.error("获取日记列表失败");
        }
    }

    @GetMapping("/mood-chart")
    public Result<List<MoodChartDTO>> getMoodChart(@RequestParam Long userId) {
        try {
            List<MoodChartDTO> chartData = diaryService.getMoodChartData(userId);
            log.info("获取用户 {} 的情绪图表数据，共 {} 条记录", userId, chartData.size());
            return Result.success(chartData);
        } catch (Exception e) {
            log.error("获取情绪图表数据失败", e);
            return Result.error("获取情绪图表数据失败");
        }
    }

    @DeleteMapping("/diary")
    public Result<Void> deleteDiary(@RequestParam Long id) {
        try {
            boolean removed = diaryService.removeById(id);
            if (removed) {
                log.info("删除日记成功，ID: {}", id);
                return Result.success("删除成功", null);
            } else {
                log.error("删除日记失败，ID: {}", id);
                return Result.error("删除日记失败");
            }
        } catch (Exception e) {
            log.error("删除日记失败", e);
            return Result.error("删除日记失败: " + e.getMessage());
        }
    }

    @PutMapping("/diary")
    public Result<Diary> updateDiary(@Valid @RequestBody DiaryDTO diaryDTO) {
        try {
            if (diaryDTO.getId() == null) {
                return Result.error("日记ID不能为空");
            }

            // 检查日记是否存在
            Diary existingDiary = diaryService.getById(diaryDTO.getId());
            if (existingDiary == null) {
                return Result.error("日记不存在");
            }

            log.info("=== 更新前的日记数据 ===");
            log.info("ID: {}, 用户ID: {}", existingDiary.getId(), existingDiary.getUserId());
            log.info("内容: {}", existingDiary.getContent());
            log.info("心情: {}", existingDiary.getMoodTag());
            log.info("创建时间: {}, 更新时间: {}",
                    existingDiary.getCreateTime(), existingDiary.getUpdateTime());

            // 使用最可靠的更新方法 - 直接使用SQL更新
            log.info("使用简单更新方法（直接SQL）");
            boolean updated = diaryService.simpleUpdateDiary(diaryDTO);

            if (updated) {
                log.info("日记更新成功，ID: {}", diaryDTO.getId());
                // 重新查询获取更新后的完整数据
                Diary updatedDiary = diaryService.getById(diaryDTO.getId());
                log.info("=== 更新后的日记数据 ===");
                log.info("ID: {}, 用户ID: {}", updatedDiary.getId(), updatedDiary.getUserId());
                log.info("内容: {}", updatedDiary.getContent());
                log.info("心情: {}", updatedDiary.getMoodTag());
                log.info("创建时间: {}, 更新时间: {}",
                        updatedDiary.getCreateTime(), updatedDiary.getUpdateTime());

                // 检查时间是否真的更新了
                if (existingDiary.getUpdateTime().equals(updatedDiary.getUpdateTime())) {
                    log.warn("警告：更新时间没有改变！原时间: {}, 新时间: {}",
                            existingDiary.getUpdateTime(), updatedDiary.getUpdateTime());
                } else {
                    log.info("更新时间已成功更新: {} -> {}",
                            existingDiary.getUpdateTime(), updatedDiary.getUpdateTime());
                }

                return Result.success("日记更新成功", updatedDiary);
            } else {
                log.error("日记更新失败，ID: {}", diaryDTO.getId());
                return Result.error("日记更新失败");
            }
        } catch (Exception e) {
            log.error("更新日记失败", e);
            return Result.error("更新日记失败: " + e.getMessage());
        }
    }

    // 新增：获取单个日记的接口
    @GetMapping("/diary")
    public Result<Diary> getDiary(@RequestParam Long id) {
        try {
            Diary diary = diaryService.getById(id);
            if (diary != null) {
                log.info("获取日记成功，ID: {}, 更新时间: {}", id, diary.getUpdateTime());
                return Result.success(diary);
            } else {
                log.error("日记不存在，ID: {}", id);
                return Result.error("日记不存在");
            }
        } catch (Exception e) {
            log.error("获取日记失败", e);
            return Result.error("获取日记失败");
        }
    }
}