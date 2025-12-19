package com.example.qxb.model;

import java.util.List;
import com.example.qxb.models.Article;

public class PageResponse<T> {

    private int code;
    private String message;
    private List<T> data;   //接收分页列表

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<T> getData() {
        return data;
    }
}

