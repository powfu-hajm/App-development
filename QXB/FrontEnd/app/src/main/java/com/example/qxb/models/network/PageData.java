package com.example.qxb.models.network;

import java.util.List;

public class PageData<T> {
    private List<T> records;
    private long total;
    private int pageNum;
    private int pageSize;

    public List<T> getRecords() { return records; }
    public long getTotal() { return total; }
    public int getPageNum() { return pageNum; }
    public int getPageSize() { return pageSize; }
}