package org.example.emotionbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.example.emotionbackend.entity.Article;
import org.example.emotionbackend.mapper.ArticleMapper;
import org.example.emotionbackend.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public IPage<Article> page(Integer pageNum, Integer pageSize, Integer typeId) {

        String key = buildCacheKey(pageNum, pageSize, typeId);

        // ====================
        // ① Redis查询缓存
        // ====================
        Object cachedResult = redisTemplate.opsForValue().get(key);
        if (cachedResult != null) {
            System.out.println("查询命中缓存：" + key);
            return (IPage<Article>) cachedResult;
        }

        // ====================
        // ② 缓存不存在 → DB分页查询
        // ====================

        Page<Article> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Article> wrapper = new QueryWrapper<>();

        if(typeId != null){
            wrapper.eq("type", typeId);
        }

        wrapper.orderByDesc("create_time");

        IPage<Article> resultPage = this.baseMapper.selectPage(page, wrapper);

        // ====================
        // ③ 写入缓存（带过期时间）
        // ====================
        long expire = 60 + (long)(Math.random() * 60); // 随机60~120秒避免雪崩
        redisTemplate.opsForValue().set(key, resultPage, expire, TimeUnit.SECONDS);

        System.out.println("写入缓存：" + key);

        return resultPage;
    }

    private String buildCacheKey(Integer pageNum, Integer pageSize, Integer typeId){
        return "article:page:type:" + (typeId == null ? "all" : typeId)
                + ":p:" + pageNum
                + ":s:" + pageSize;
    }
}
