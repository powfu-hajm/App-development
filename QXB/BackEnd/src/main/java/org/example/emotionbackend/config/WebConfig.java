package org.example.emotionbackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private TokenInterceptor tokenInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")  // ⭐ 让前端能获取 token
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")           // 拦截所有请求
                .excludePathPatterns(
                        "/user/login",           // 放行登录
                        "/user/register",        // 放行注册
                        "/uploads/**",
                        "/error",
                        "/v3/api-docs/**",
                        "/ai/test",
                        "/article/crawl",        // 放行爬虫接口
                        "/article/list",         // 放行文章列表
                        "/article/page",         // 放行文章分页
                        "/article/**",           // 放行所有文章相关接口（包括详情）
                        "/test/papers",          // 测试问卷列表无需登录
                        "/test/paper/**",        // 问卷详情无需登录
                        "/swagger-ui/**"
                );
    }
}