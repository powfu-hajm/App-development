package org.example.emotionbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling  // ⭐ 启用定时任务
@SpringBootApplication
@MapperScan("org.example.emotionbackend.mapper")
public class QXBBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(QXBBackendApplication.class, args);
        System.out.println("🚀 后端启动成功，定时任务已启用");
    }
}
