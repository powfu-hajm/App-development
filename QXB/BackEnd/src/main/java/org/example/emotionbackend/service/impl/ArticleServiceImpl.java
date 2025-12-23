package org.example.emotionbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.emotionbackend.entity.Article;
import org.example.emotionbackend.mapper.ArticleMapper;
import org.example.emotionbackend.service.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;
import org.example.emotionbackend.common.PageData;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private static final Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public PageData<Article> page(Integer pageNum, Integer pageSize, Integer typeId) {

        // =============== 尝试使用缓存，如果Redis不可用则降级 ===============
        String key = buildCacheKey(pageNum, pageSize, typeId);

        if (redisTemplate != null) {
            try {
                Object cachedResult = redisTemplate.opsForValue().get(key);
                if (cachedResult != null) {
                    logger.info("查询命中缓存：" + key);
                    return (PageData<Article>) cachedResult;
                }
            } catch (Exception e) {
                logger.warn("Redis查询失败，降级到数据库查询: {}", e.getMessage());
            }
        }

        // =============== DB分页查询 ===============
        Page<Article> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Article> wrapper = new QueryWrapper<>();

        // 调试日志：打印查询参数
        logger.info("执行分页查询: pageNum={}, pageSize={}, typeId={}", pageNum, pageSize, typeId);

        // 使用正确的字段名 - type字段是字符串
        if (typeId != null && typeId > 0) {
            // 根据 typeId 获取对应的 type 字符串值
            String typeValue = convertTypeIdToString(typeId);
            if (typeValue != null) {
                wrapper.eq("type", typeValue);  // 使用字符串字段名
                logger.info("添加类型过滤条件: type='{}' (typeId={})", typeValue, typeId);
            } else {
                logger.warn("无效的typeId: {}", typeId);
            }
        } else {
            logger.info("无类型过滤，查询所有文章");
        }

        // 根据 create_time 排序，不是 id
        wrapper.orderByDesc("create_time");

        IPage<Article> resultPage;

        try {
            // 直接使用 baseMapper.selectPage
            resultPage = this.baseMapper.selectPage(page, wrapper);
            logger.info("数据库查询完成: total={}, pages={}, size={}",
                    resultPage.getTotal(), resultPage.getPages(), resultPage.getSize());

            // 打印查询到的文章信息
            if (!resultPage.getRecords().isEmpty()) {
                logger.info("查询到 {} 篇文章", resultPage.getRecords().size());
                for (Article article : resultPage.getRecords()) {
                    logger.debug("文章: id={}, title='{}', type='{}'",
                            article.getId(), article.getTitle(), article.getType());
                }
            } else {
                logger.warn("数据库查询返回空结果");
            }
        } catch (Exception e) {
            logger.error("分页查询失败: {}", e.getMessage(), e);
            // 降级处理：不按类型过滤
            wrapper.clear();  // 清除之前的条件
            wrapper.orderByDesc("create_time");
            resultPage = this.baseMapper.selectPage(page, wrapper);
            logger.info("降级查询完成，查询到 {} 篇文章", resultPage.getRecords().size());
        }

        PageData<Article> pageData = new PageData<>(
                resultPage.getRecords(),
                resultPage.getTotal(),
                (int) resultPage.getCurrent(),
                (int) resultPage.getSize()
        );

        // =============== 尝试写入缓存 ===============
        if (redisTemplate != null) {
            try {
                long expire = 60 + (long)(Math.random() * 60);
                redisTemplate.opsForValue().set(key, pageData, expire, TimeUnit.SECONDS);
                logger.info("写入缓存：{} (过期时间: {}秒)", key, expire);
            } catch (Exception e) {
                logger.warn("Redis写入失败: {}", e.getMessage());
            }
        }

        return pageData;
    }

    /**
     * 将typeId转换为对应的类型字符串
     */
    private String convertTypeIdToString(Integer typeId) {
        if (typeId == null) return null;

        switch (typeId) {
            case 1: return "专栏";
            case 2: return "测试";
            case 3: return "科普";
            case 4: return "自我成长";
            case 5: return "情感关系";
            default:
                logger.warn("未知的typeId: {}", typeId);
                return null;
        }
    }

    private String buildCacheKey(Integer pageNum, Integer pageSize, Integer typeId){
        return "article:page:type:" + (typeId == null ? "all" : typeId)
                + ":p:" + pageNum
                + ":s:" + pageSize;
    }
}