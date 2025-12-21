package org.example.emotionbackend.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.emotionbackend.common.PageData;
import org.example.emotionbackend.entity.Article;
import org.example.emotionbackend.service.ArticleService;
import org.example.emotionbackend.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    private ArticleService articleService;

    /**
     * 根据分页参数和类型获取首页内容（如文章/视频等）
     */
    @Override
    public PageData<Article> getPagedContent(Integer pageNum, Integer pageSize, String type) {

        Integer typeId = null;

        // 简单映射类型 -> 数据库 typeId
        if(type != null){
            switch (type){
                case "article": typeId = 1; break;
                case "video": typeId = 2; break;
                case "music": typeId = 3; break;
            }
        }

        // 调用 ArticleService 的分页方法
        IPage<Article> page = articleService.page(pageNum, pageSize, typeId);

        // 封装给前端返回
        return new PageData<>(
                page.getRecords(),   // 当前页数据
                page.getTotal(),     // 总条数
                pageNum,             // 当前页码
                pageSize             // 每页条数
        );
    }
}
