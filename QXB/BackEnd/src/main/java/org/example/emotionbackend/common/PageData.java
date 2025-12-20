package org.example.emotionbackend.common;

import lombok.Data;
import java.util.List;

@Data
public class PageData<T> {
    private long total;
    private List<T> list;

    public PageData(long total, List<T> list){
        this.total = total;
        this.list = list;
    }
}
