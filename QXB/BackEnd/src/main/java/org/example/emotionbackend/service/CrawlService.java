package org.example.emotionbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.emotionbackend.entity.Article;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.example.emotionbackend.mapper.ArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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

    private static final String TARGET_URL = "https://www.xinli001.com/info";


    /**
     * 定时爬虫任务（每小时执行一次）
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void scheduleCrawl() {
        log.info("⏳ 定时任务触发：开始爬取文章...");
        crawlArticles();
    }


    /**
     * 真正的爬虫逻辑
     */
    public List<Article> crawlArticles() {

        List<Article> articles = new ArrayList<>();

        try {
            log.info("开始爬取文章: {}", TARGET_URL);

            Document doc = Jsoup.connect(TARGET_URL)
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();

            Elements itemElements = doc.select(".common-left .item");

            if (itemElements.isEmpty()) {
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
                        title = item.text();
                        link = item.attr("abs:href");
                    } else {
                        Element titleEl = item.selectFirst(".title a");
                        if (titleEl == null) continue;

                        title = titleEl.text();
                        link = titleEl.attr("abs:href");

                        Element descEl = item.selectFirst(".des");
                        if (descEl != null) summary = descEl.text();

                        Element imgEl = item.selectFirst(".img img");
                        if (imgEl != null) coverUrl = imgEl.attr("abs:src");
                    }

                    if (title.length() < 2 || link.isEmpty()) continue;

                    Long count = articleMapper.selectCount(
                            new LambdaQueryWrapper<Article>().eq(Article::getOriginalUrl, link)
                    );

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
                    log.warn("解析失败:", e);
                }
            }

            log.info("成功抓取 {} 篇文章", articles.size());

        } catch (IOException e) {
            log.error("爬取失败", e);
        }

        return articles;
    }
}

