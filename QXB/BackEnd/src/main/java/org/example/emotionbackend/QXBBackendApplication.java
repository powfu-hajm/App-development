package org.example.emotionbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
@MapperScan("org.example.emotionbackend.mapper")
@EnableScheduling  // 通过配置(@Value)控制是否执行
public class QXBBackendApplication {

    public static boolean enableCrawl = false;  // 全局开关

    public QXBBackendApplication(@Value("${crawler.enable:false}") boolean enable) {
        enableCrawl = enable;
        System.out.println(enable ? "爬虫自动任务已开启" : "爬虫已禁用（仅手动触发）");
    }

    public static void main(String[] args) {
        SpringApplication.run(QXBBackendApplication.class, args);
        System.out.println("后端启动成功");
    }
}
