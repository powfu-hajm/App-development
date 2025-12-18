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
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry registry) {
        String path = System.getProperty("user.dir");
        // 简单处理路径，确保以 / 结尾
        String uploadPath = "file:" + path + "/uploads/";
        System.out.println("Static Resource Path: " + uploadPath); // 打印路径到控制台

        // 将 /uploads/** 映射到本地文件系统的 uploads 目录
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**") // 拦截所有接口
                .excludePathPatterns(
                        "/user/login",          // 放行登录 (去掉/api)
                        "/user/register",       // 放行注册 (去掉/api)
                        "/article/**",          // 放行文章相关接口 (去掉/api)
                        "/test/**",             // 放行测试接口 (本身就没有/api)
                        "/ai/test",             // 放行测试接口
                        "/error",               // 放行错误页面
                        "/uploads/**",          // 放行静态资源
                        "/v3/api-docs/**",      // 放行 Swagger 文档(如果有)
                        "/swagger-ui/**"
                );
    }
}
