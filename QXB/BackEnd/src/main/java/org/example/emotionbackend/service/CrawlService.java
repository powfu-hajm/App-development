package org.example.emotionbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.emotionbackend.entity.Article;
import org.example.emotionbackend.mapper.ArticleMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CrawlService {

    @Autowired
    private ArticleMapper articleMapper;

    // 目标网站：壹心理 - 心理资讯
    private static final String TARGET_URL = "https://www.xinli001.com/info";

    public List<Article> crawlArticles() {
        List<Article> articles = new ArrayList<>();
        try {
            log.info("开始爬取文章: {}", TARGET_URL);
            
            // 模拟浏览器 User-Agent
            Document doc = Jsoup.connect(TARGET_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000)
                    .get();

            // 壹心理的结构：通常文章列表在 .common-left 下的 .item
            // 标题在 .title a
            // 简介在 .des
            // 图片在 .img img
            
            Elements itemElements = doc.select(".common-left .item");
            
            if (itemElements.isEmpty()) {
                // 备用策略：直接找所有标题链接
                itemElements = doc.select("a.title");
            }

            log.info("找到潜在文章块数: {}", itemElements.size());

            for (Element item : itemElements) {
                try {
                    String title;
                    String link;
                    String summary = "";
                    String coverUrl = "";

                    if (item.tagName().equals("a")) {
                        // 备用策略的情况
                        title = item.text();
                        link = item.attr("abs:href");
                    } else {
                        // 标准结构情况
                        Element titleEl = item.selectFirst(".title a");
                        if (titleEl == null) continue;
                        
                        title = titleEl.text();
                        link = titleEl.attr("abs:href");
                        
                        Element descEl = item.selectFirst(".des");
                        if (descEl != null) summary = descEl.text();
                        
                        Element imgEl = item.selectFirst(".img img");
                        if (imgEl != null) coverUrl = imgEl.attr("abs:src");
                    }

                    // 过滤无效数据
                    if (title.length() < 2 || link.isEmpty()) continue;

                    // 查重
                    Long count = articleMapper.selectCount(new LambdaQueryWrapper<Article>()
                            .eq(Article::getOriginalUrl, link));
                    if (count > 0) continue;

                    Article article = new Article();
                    article.setTitle(title);
                    article.setOriginalUrl(link);
                    article.setSource("壹心理");
                    article.setSummary(summary.isEmpty() ? "点击查看详情..." : summary);
                    article.setCoverUrl(coverUrl);
                    article.setReadCount((int) (Math.random() * 5000) + 500);
                    article.setPublishTime(LocalDateTime.now());
                    article.setCreateTime(LocalDateTime.now());

                    articles.add(article);
                    articleMapper.insert(article);
                    
                } catch (Exception e) {
                    log.warn("解析单篇文章失败", e);
                }
            }
            
            log.info("成功抓取并保存 {} 篇文章", articles.size());

        } catch (IOException e) {
            log.error("爬取失败", e);
        }
        return articles;
    }
}
