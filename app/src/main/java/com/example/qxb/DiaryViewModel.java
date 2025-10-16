package com.example.qxb;

import androidx.lifecycle.ViewModel;
import java.util.Date;

public class DiaryViewModel extends ViewModel {

    public void saveDiary(String content, Date date) {
        // 这里实现日记保存逻辑
        System.out.println("保存日记内容: " + content);
        System.out.println("保存时间: " + date);
        // 可以连接到数据库、文件或网络API
    }
}