package org.example.emotionbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.example.emotionbackend.mapper")
public class QXBBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(QXBBackendApplication.class, args);
    }

}