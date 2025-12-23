package org.example.emotionbackend.controller;

import jakarta.annotation.Resource;
import org.example.emotionbackend.common.ApiResponse;
import org.example.emotionbackend.common.PageData;
import org.example.emotionbackend.entity.Article;
import org.example.emotionbackend.mapper.ArticleMapper;
import org.example.emotionbackend.service.ArticleService;
import org.example.emotionbackend.service.CrawlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article")
public class ArticleController {

    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    @Resource
    private ArticleService articleService;

    @Autowired
    private CrawlService crawlService;

    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 爬虫触发接口
     */
    @GetMapping("/crawl")
    public ApiResponse<List<Article>> triggerCrawl() {
        try {
            logger.info("触发文章爬取");
            List<Article> articles = crawlService.crawlArticles();
            return ApiResponse.success(articles);
        } catch (Exception e) {
            logger.error("爬取文章失败: {}", e.getMessage(), e);
            return ApiResponse.failed(500, "爬取文章失败: " + e.getMessage());
        }
    }

    /**
     * 查询所有文章列表
     */
    @GetMapping("/list")
    public ApiResponse<List<Article>> getArticles() {
        try {
            logger.info("查询所有文章列表");
            List<Article> articles = articleMapper.selectList(null);
            logger.info("查询到 {} 篇文章", articles.size());
            return ApiResponse.success(articles);
        } catch (Exception e) {
            logger.error("查询文章列表失败: {}", e.getMessage(), e);
            return ApiResponse.failed(500, "查询文章列表失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询文章
     * 参数说明：
     *   page: 页码，从1开始，默认为1
     *   size: 每页大小，默认为10
     *   typeId: 文章类型ID，可选，1=专栏, 2=测试, 3=科普, 4=自我成长, 5=情感关系
     */
    @GetMapping("/page")
    public ApiResponse<PageData<Article>> getArticlesPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer typeId) {

        try {
            logger.info("分页查询文章: page={}, size={}, typeId={}", page, size, typeId);

            if (page < 1) page = 1;
            if (size < 1) size = 10;
            if (size > 100) size = 100; // 限制每页最大100条

            PageData<Article> result = articleService.page(page, size, typeId);

            // 添加详细的日志
            logger.info("分页查询完成: total={}, pages={}, current={}, size={}, records={}",
                    result.getTotal(), result.getPages(), result.getPageNum(),
                    result.getPageSize(), result.getRecords().size());

            // 记录前几条文章的标题和类型
            if (!result.getRecords().isEmpty()) {
                for (int i = 0; i < Math.min(3, result.getRecords().size()); i++) {
                    Article article = result.getRecords().get(i);
                    logger.info("文章 {}: 标题='{}', 类型='{}', typeId={}",
                            i + 1, article.getTitle(), article.getType(), article.getTypeId());
                }
            }

            return ApiResponse.success(result);
        } catch (Exception e) {
            logger.error("分页查询文章失败: {}", e.getMessage(), e);
            return ApiResponse.failed(500, "分页查询文章失败: " + e.getMessage());
        }
    }

    /**
     * 获取文章详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Article> getArticleDetail(@PathVariable Long id) {
        try {
            logger.info("获取文章详情: id={}", id);
            Article article = articleMapper.selectById(id);

            if (article == null) {
                logger.warn("文章不存在: id={}", id);
                return ApiResponse.failed(404, "文章不存在");
            }

            return ApiResponse.success(article);
        } catch (Exception e) {
            logger.error("获取文章详情失败: {}", e.getMessage(), e);
            return ApiResponse.failed(500, "获取文章详情失败: " + e.getMessage());
        }
    }
}