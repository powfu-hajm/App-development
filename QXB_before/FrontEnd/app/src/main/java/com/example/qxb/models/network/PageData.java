package com.example.qxb.models.network;

import java.util.List;

public class PageData<T> {
    private int page;
    private int size;
    private long total;
    private List<T> data;

    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotal() { return total; }
    public List<T> getData() { return data; }
}
