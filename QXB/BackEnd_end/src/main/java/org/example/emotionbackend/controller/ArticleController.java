package org.example.emotionbackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import org.example.emotionbackend.mapper.ArticleMapper;

import org.example.emotionbackend.common.Result;
import org.example.emotionbackend.entity.Article;
import org.example.emotionbackend.service.ArticleService;
import org.example.emotionbackend.service.CrawlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;



@RestController
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private ArticleService articleService;

    @Autowired
    private CrawlService crawlService;

    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 手动触发爬虫 (管理员用)
     */
    @GetMapping("/crawl")
    public Result<List<Article>> triggerCrawl() {
        List<Article> articles = crawlService.crawlArticles();
        return Result.success(articles);
    }
    /**
     * 获取文章列表
     */
    @GetMapping("/list")
    public Result<List<Article>> getArticles() {
        List<Article> articles = articleMapper.selectList(null);
        return Result.success(articles);
    }
    /**
     * 分页查询文章
     * 支持按类别分页: 0推文，1视频，2音乐
     */
    @GetMapping("/page")
    public Result<IPage<Article>> getArticlesPage(@RequestParam Integer pageNum,
                                                  @RequestParam Integer pageSize,
                                                  @RequestParam(required = false) Integer type) {
        IPage<Article> articlePage = articleService.page(pageNum, pageSize, type);
        return Result.success(articlePage);
    }

}