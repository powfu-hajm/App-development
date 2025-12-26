package org.example.emotionbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.emotionbackend.entity.MbtiType;

/**
 * MBTI 类型 Mapper
 */
@Mapper
public interface MbtiTypeMapper extends BaseMapper<MbtiType> {

    /**
     * 根据类型代码查询MBTI类型
     */
    @Select("SELECT * FROM mbti_type WHERE type_code = #{typeCode} AND deleted = 0")
    MbtiType selectByTypeCode(@Param("typeCode") String typeCode);
}
