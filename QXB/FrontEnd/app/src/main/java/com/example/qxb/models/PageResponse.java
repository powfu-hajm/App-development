package com.example.qxb.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PageResponse<T> {
    @SerializedName("records")
    private List<T> records;

    @SerializedName("total")
    private long total;

    @SerializedName("pages")
    private long pages;

    @SerializedName("pageNum")
    private Integer pageNum;

    @SerializedName("pageSize")
    private Integer pageSize;

    public PageResponse() {}

    // Getters and Setters
    public List<T> getRecords() { return records; }
    public void setRecords(List<T> records) { this.records = records; }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public long getPages() { return pages; }
    public void setPages(long pages) { this.pages = pages; }

    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }

    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }

    public boolean hasData() {
        return records != null && !records.isEmpty();
    }

    public int getItemCount() {
        return records != null ? records.size() : 0;
    }
}