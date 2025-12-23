package org.example.emotionbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.emotionbackend.QXBBackendApplication;
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
        if (!QXBBackendApplication.enableCrawl) {
            log.info("爬虫功能已禁用，跳过定时任务");
            return;
        }
        log.info("定时任务触发：开始爬取文章...");
        crawlArticles();
    }

    /**
     * 爬取文章
     */
    public List<Article> crawlArticles() {

        List<Article> articles = new ArrayList<>();

        try {
            log.info("开始爬取文章: {}", TARGET_URL);

            Document doc = Jsoup.connect(TARGET_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(15000)
                    .get();

            Elements itemElements = doc.select(".article-item, .item, .article-list li, a.title");

            log.info("找到潜在文章块数: {}", itemElements.size());

            int savedCount = 0;
            for (Element item : itemElements) {
                try {
                    String title = null;
                    String link = null;
                    String summary = "";
                    String coverUrl = "";

                    // 多种选择器尝试获取标题和链接
                    Element titleEl = item.selectFirst("a[href*=/info/], .title a, h2 a, h3 a");
                    if (titleEl != null) {
                        title = titleEl.text().trim();
                        link = titleEl.attr("abs:href");
                    }

                    // 如果还没找到，尝试其他选择器
                    if ((title == null || title.isEmpty()) && item.hasAttr("href")) {
                        title = item.text().trim();
                        link = item.attr("abs:href");
                    }

                    if (title == null || title.length() < 2 || link == null || link.isEmpty()) {
                        continue;
                    }

                    // 检查是否已存在
                    Long count = articleMapper.selectCount(
                            new LambdaQueryWrapper<Article>().eq(Article::getOriginalUrl, link)
                    );
                    if (count > 0) {
                        log.debug("文章已存在，跳过: {}", title);
                        continue;
                    }

                    // 尝试获取摘要
                    Element descEl = item.selectFirst(".des, .summary, .description, .content");
                    if (descEl != null) {
                        summary = descEl.text().trim();
                        if (summary.length() > 200) {
                            summary = summary.substring(0, 200) + "...";
                        }
                    }

                    // 尝试获取封面图
                    Element imgEl = item.selectFirst("img[src]");
                    if (imgEl != null) {
                        coverUrl = imgEl.attr("abs:src");
                    }

                    // 访问文章详情页面获取更多信息
                    String category = "专栏"; // 默认值
                    try {
                        Document detailDoc = Jsoup.connect(link)
                                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                                .timeout(10000)
                                .get();

                        // 尝试获取分类
                        Elements breadcrumbs = detailDoc.select(".breadcrumb a, .category a, .tags a");
                        for (Element breadcrumb : breadcrumbs) {
                            String text = breadcrumb.text().trim();
                            if (!text.isEmpty() && !text.equals("首页") && !text.equals("文章")) {
                                category = text;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        log.warn("无法获取文章详情: {}", e.getMessage());
                    }

                    // 构造Article对象
                    Article article = new Article();
                    article.setTitle(title);
                    article.setOriginalUrl(link);
                    article.setSource("壹心理");
                    article.setSummary(summary.isEmpty() ? "点击查看详情..." : summary);
                    article.setCoverUrl(coverUrl);
                    article.setReadCount((int) (Math.random() * 5000) + 500);
                    article.setPublishTime(LocalDateTime.now());
                    article.setCreateTime(LocalDateTime.now());

                    // 设置type字段（根据category映射）
                    String type = mapCategoryToType(category);
                    article.setType(type);  // 直接设置type字段

                    log.info("文章信息: 标题='{}', 分类='{}', 映射类型='{}'", title, category, type);

                    // 插入数据库
                    articleMapper.insert(article);
                    articles.add(article);
                    savedCount++;

                    log.info("成功保存文章: {}，类型: {}", title, type);

                    // 避免请求过快
                    Thread.sleep(500);

                } catch (Exception e) {
                    log.warn("解析单篇文章失败: {}", e.getMessage());
                }
            }

            log.info("成功抓取 {} 篇文章，实际保存 {} 篇", itemElements.size(), savedCount);

        } catch (IOException e) {
            log.error("爬取失败", e);
        } catch (Exception e) {
            log.error("爬取过程异常", e);
        }

        return articles;
    }

    /**
     * 将分类映射到type字段
     */
    private String mapCategoryToType(String category) {
        if (category == null || category.isEmpty()) {
            return "专栏";
        }

        String lowerCategory = category.toLowerCase();

        if (lowerCategory.contains("测试") || lowerCategory.contains("测评") ||
                lowerCategory.contains("问卷") || lowerCategory.contains("测验")) {
            return "测试";
        } else if (lowerCategory.contains("科普") || lowerCategory.contains("知识") ||
                lowerCategory.contains("科学") || lowerCategory.contains("心理知识")) {
            return "科普";
        } else if (lowerCategory.contains("成长") || lowerCategory.contains("自我") ||
                lowerCategory.contains("个人") || lowerCategory.contains("发展")) {
            return "自我成长";
        } else if (lowerCategory.contains("情感") || lowerCategory.contains("关系") ||
                lowerCategory.contains("爱情") || lowerCategory.contains("婚姻") ||
                lowerCategory.contains("恋爱")) {
            return "情感关系";
        } else {
            return "专栏"; // 默认值
        }
    }
}