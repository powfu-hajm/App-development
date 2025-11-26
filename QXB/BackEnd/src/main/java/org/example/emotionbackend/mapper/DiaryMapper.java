package org.example.emotionbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.example.emotionbackend.dto.MoodChartDTO;
import org.example.emotionbackend.entity.Diary;

import java.util.List;

@Mapper
public interface DiaryMapper extends BaseMapper<Diary> {
    List<MoodChartDTO> selectMoodChartData(@Param("userId") Long userId);

    // 更新日记的方法，直接在SQL中设置update_time
    @Update("UPDATE diary SET content = #{content}, mood_tag = #{moodTag}, update_time = NOW() WHERE id = #{id} AND user_id = #{userId}")
    int updateDiaryWithTime(@Param("id") Long id, @Param("userId") Long userId, @Param("content") String content, @Param("moodTag") String moodTag);
}