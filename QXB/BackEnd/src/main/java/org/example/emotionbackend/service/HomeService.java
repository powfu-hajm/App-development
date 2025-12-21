package org.example.emotionbackend.service;

import org.example.emotionbackend.common.PageData;

public interface HomeService {

    PageData<?> getPagedContent(Integer pageNum, Integer pageSize, String type);
}
