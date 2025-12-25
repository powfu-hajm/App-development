package org.example.emotionbackend.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.emotionbackend.entity.Article;
import org.example.emotionbackend.mapper.ArticleMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class ArticleSyncService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private ArticleMapper articleMapper;

    private static final String VIEW_KEY_PREFIX = "article:view:";

    /**
     * 每10分钟同步一次 Redis 阅读量 → MySQL
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void syncViewsToDB() {

        try {
            log.info("开始同步文章阅读量到数据库...");

            // 先测试Redis连接
            if (!isRedisAvailable()) {
                log.warn("Redis不可用，跳过同步");
                return;
            }

            // 1️⃣ 找到所有阅读量 key
            Set<String> keys = redisTemplate.keys(VIEW_KEY_PREFIX + "*");

            if (keys == null || keys.isEmpty()) {
                log.info("暂无阅读量缓存可同步");
                return;
            }

            for (String key : keys) {
                try {
                    Long id = Long.valueOf(key.replace(VIEW_KEY_PREFIX, ""));
                    Integer inc = (Integer) redisTemplate.opsForValue().get(key);

                    if (inc == null) continue;

                    // MySQL 更新阅读量（自增）
                    articleMapper.increaseReadCount(id, inc);
                    redisTemplate.delete(key);

                    log.info("已同步文章 id={} 阅读量+{}", id, inc);
                } catch (Exception e) {
                    log.error("同步单个文章阅读量失败: {}", e.getMessage());
                }
            }

            log.info("阅读量同步完成");
        } catch (Exception e) {
            log.error("阅读量同步任务失败: {}", e.getMessage());
        }
    }

    /**
     * 检查Redis是否可用
     */
    private boolean isRedisAvailable() {
        try {
            // 尝试执行一个简单的Redis命令来测试连接
            redisTemplate.getConnectionFactory().getConnection().ping();
            return true;
        } catch (Exception e) {
            log.warn("Redis连接不可用: {}", e.getMessage());
            return false;
        }
    }
}