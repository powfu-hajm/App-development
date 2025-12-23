package org.example.emotionbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import org.example.emotionbackend.entity.Article;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * Redis 阅读量同步：数据库阅读数字段自增
     */
    @Update("UPDATE article SET read_count = read_count + #{inc} WHERE id = #{id}")
    void increaseReadCount(@Param("id") Long id, @Param("inc") Integer inc);

    @Update("UPDATE article SET read_count = read_count + #{readCount} WHERE id = #{id}")
    int incrementViews(@Param("id") Long id,
                       @Param("readCount") Integer readCount);
}