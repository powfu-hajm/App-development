package org.example.emotionbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.emotionbackend.entity.Article;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 自定义分页查询
     */
    List<Article> page(@Param("offset") int offset,
                       @Param("size") int size,
                       @Param("type") int type);

    /**
     * 统计类型总数
     */
    int count(@Param("type") int type);
}
