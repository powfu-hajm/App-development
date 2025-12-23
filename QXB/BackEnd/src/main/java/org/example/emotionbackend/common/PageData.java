package org.example.emotionbackend.common;

import java.io.Serializable;
import java.util.List;

public class PageData<T> implements Serializable {

    private List<T> records;     // 当前页数据
    private long total;          // 总记录数
    private long pages;          // 总页数（新增）
    private Integer pageNum;     // 当前页码
    private Integer pageSize;    // 每页条数

    public PageData() {}

    /** 构造分页数据（推荐调用此构造）*/
    public PageData(List<T> records, long total, Integer pageNum, Integer pageSize) {
        this.records = records;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = (long) Math.ceil((double) total / pageSize);
    }

    // getter/setter
    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
