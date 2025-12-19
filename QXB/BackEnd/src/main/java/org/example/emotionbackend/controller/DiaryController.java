package org.example.emotionbackend.controller;

import org.example.emotionbackend.common.Result;
import org.example.emotionbackend.dto.DiaryDTO;
import org.example.emotionbackend.dto.MoodChartDTO;
import org.example.emotionbackend.entity.Diary;
import org.example.emotionbackend.service.IDiaryService;
import org.example.emotionbackend.utils.UserContext;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/")
public class DiaryController {

    @Autowired
    private IDiaryService diaryService;

    @PostMapping("/diary")
    public Result<Diary> createDiary(@Valid @RequestBody DiaryDTO diaryDTO) {
        try {
            Long userId = UserContext.getUserId();
            if (userId == null) {
                return Result.error("未登录");
            }

            Diary diary = new Diary();
            BeanUtils.copyProperties(diaryDTO, diary);
            // 强制设置当前用户ID
            diary.setUserId(userId);

            log.info("创建日记 - 用户ID: {}, 内容: {}, 心情: {}",
                    diary.getUserId(), diary.getContent(), diary.getMoodTag());

            boolean saved = diaryService.save(diary);
            if (saved) {
                log.info("日记保存成功，ID: {}", diary.getId());
                Diary savedDiary = diaryService.getById(diary.getId());
                return Result.success("日记创建成功", savedDiary);
            } else {
                return Result.error("日记保存失败");
            }
        } catch (Exception e) {
            log.error("创建日记失败", e);
            return Result.error("创建日记失败: " + e.getMessage());
        }
    }

    @GetMapping("/diaries")
    public Result<List<Diary>> getDiaries(@RequestParam(required = false) Long userId) {
        // 忽略前端传来的 userId，强制使用当前登录用户
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            return Result.error("未登录");
        }

        try {
            List<Diary> diaries = diaryService.lambdaQuery()
                    .eq(Diary::getUserId, currentUserId)
                    .orderByDesc(Diary::getCreateTime)
                    .list();

            log.info("获取用户 {} 的日记列表，共 {} 条记录", currentUserId, diaries.size());
            return Result.success(diaries);
        } catch (Exception e) {
            log.error("获取日记列表失败", e);
            return Result.error("获取日记列表失败");
        }
    }

    @GetMapping("/mood-chart")
    public Result<List<MoodChartDTO>> getMoodChart(@RequestParam(required = false) Long userId) {
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            return Result.error("未登录");
        }

        try {
            List<MoodChartDTO> chartData = diaryService.getMoodChartData(currentUserId);
            log.info("获取用户 {} 的情绪图表数据，共 {} 条记录", currentUserId, chartData.size());
            return Result.success(chartData);
        } catch (Exception e) {
            log.error("获取情绪图表数据失败", e);
            return Result.error("获取情绪图表数据失败");
        }
    }

    @DeleteMapping("/diary")
    public Result<Void> deleteDiary(@RequestParam Long id) {
        Long userId = UserContext.getUserId();
        try {
            // 安全检查：只能删除自己的日记
            Diary diary = diaryService.getById(id);
            if (diary == null) {
                return Result.error("日记不存在");
            }
            if (!diary.getUserId().equals(userId)) {
                return Result.error("无权删除此日记");
            }

            boolean removed = diaryService.removeById(id);
            if (removed) {
                log.info("删除日记成功，ID: {}", id);
                return Result.success("删除成功", null);
            } else {
                return Result.error("删除日记失败");
            }
        } catch (Exception e) {
            log.error("删除日记失败", e);
            return Result.error("删除日记失败: " + e.getMessage());
        }
    }

    @PutMapping("/diary")
    public Result<Diary> updateDiary(@Valid @RequestBody DiaryDTO diaryDTO) {
        Long userId = UserContext.getUserId();
        try {
            if (diaryDTO.getId() == null) {
                return Result.error("日记ID不能为空");
            }

            // 安全检查：只能修改自己的日记
            Diary existingDiary = diaryService.getById(diaryDTO.getId());
            if (existingDiary == null) {
                return Result.error("日记不存在");
            }
            if (!existingDiary.getUserId().equals(userId)) {
                return Result.error("无权修改此日记");
            }

            // 使用最可靠的更新方法 - 直接使用SQL更新
            log.info("使用简单更新方法（直接SQL）");
            boolean updated = diaryService.simpleUpdateDiary(diaryDTO);

            if (updated) {
                log.info("日记更新成功，ID: {}", diaryDTO.getId());
                Diary updatedDiary = diaryService.getById(diaryDTO.getId());
                return Result.success("日记更新成功", updatedDiary);
            } else {
                return Result.error("日记更新失败");
            }
        } catch (Exception e) {
            log.error("更新日记失败", e);
            return Result.error("更新日记失败: " + e.getMessage());
        }
    }

    @GetMapping("/diary")
    public Result<Diary> getDiary(@RequestParam Long id) {
        Long userId = UserContext.getUserId();
        try {
            Diary diary = diaryService.getById(id);
            if (diary != null) {
                // 安全检查
                if (!diary.getUserId().equals(userId)) {
                    return Result.error("无权查看此日记");
                }
                return Result.success(diary);
            } else {
                return Result.error("日记不存在");
            }
        } catch (Exception e) {
            log.error("获取日记失败", e);
            return Result.error("获取日记失败");
        }
    }
}
