package org.example.emotionbackend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.emotionbackend.entity.Article;

public interface ArticleService extends IService<Article> {
    IPage<Article> page(Integer pageNum, Integer pageSize, Integer typeId);
}
