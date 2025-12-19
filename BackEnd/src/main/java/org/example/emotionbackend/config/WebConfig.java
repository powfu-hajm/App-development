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
                .addPathPatterns("/api/**")          // ⭐只拦截API接口
                .excludePathPatterns(
                        "/api/user/login",           // ⭐放行登录
                        "/api/user/register",        // ⭐放行注册
                        "/uploads/**",
                        "/error",
                        "/v3/api-docs/**",
                        "/test/**", // 放行测试接口 (本身就没有/api)
                        "/ai/test", // 放行测试接口
                        "/swagger-ui/**",
                        "/api/article/list"          // 若文章无需登录则放开
                );
    }
}

