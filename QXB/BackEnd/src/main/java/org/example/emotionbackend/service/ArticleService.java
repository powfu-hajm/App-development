package org.example.emotionbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.emotionbackend.entity.Article;
import org.example.emotionbackend.common.PageData;

public interface ArticleService extends IService<Article> {

    /**
     * 统一返回 PageData 自定义分页结构
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @param typeId   分类 id，可为空
     * @return PageData<Article> 统一分页格式
     */
    PageData<Article> page(Integer pageNum, Integer pageSize, Integer typeId);
}
