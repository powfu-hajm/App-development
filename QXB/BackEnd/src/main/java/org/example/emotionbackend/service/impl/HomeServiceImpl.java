package org.example.emotionbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.emotionbackend.common.PageData;
import org.example.emotionbackend.entity.Article;
import org.example.emotionbackend.mapper.ArticleMapper;
import org.example.emotionbackend.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public PageData<Article> getPagedContent(Integer pageNum, Integer pageSize, String type) {

        Integer typeId = null;

        switch (type) {
            case "article": typeId = 1; break;
            case "video": typeId = 2; break;
            case "music": typeId = 3; break;
        }

        Page<Article> page = new Page<>(pageNum, pageSize);

        QueryWrapper<Article> wrapper = new QueryWrapper<>();

        if(typeId != null){
            wrapper.eq("type", typeId);
        }

        IPage<Article> pageResult = articleMapper.selectPage(page, wrapper);

        PageData<Article> result = new PageData<>();
        result.setRecords(pageResult.getRecords());
        result.setTotal(pageResult.getTotal());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);

        return result;
    }
}
