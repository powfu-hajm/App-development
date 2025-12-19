package org.example.emotionbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.emotionbackend.entity.TestOption;

import java.util.List;

@Mapper
public interface TestOptionMapper extends BaseMapper<TestOption> {

    @Select("<script>" +
            "SELECT COALESCE(SUM(score), 0) FROM test_option " +
            "WHERE id IN " +
            "<foreach collection='optionIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            " AND deleted = 0" +
            "</script>")
    Integer sumScoreByIds(@Param("optionIds") List<Long> optionIds);
}
