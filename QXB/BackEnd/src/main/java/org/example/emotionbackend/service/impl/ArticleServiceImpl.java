package org.example.emotionbackend.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.emotionbackend.entity.Article;
import org.example.emotionbackend.mapper.ArticleMapper;
import org.example.emotionbackend.service.ArticleService;
import org.springframework.stereotype.Service;


@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Override
    public IPage<Article> page(Integer pageNum, Integer pageSize, Integer typeId) {
        Page<Article> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        if (typeId != null) {
            wrapper.eq(Article::getType, typeId);
        }
        return this.page(page, wrapper);
    }

}

