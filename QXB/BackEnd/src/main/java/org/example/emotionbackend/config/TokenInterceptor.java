package org.example.emotionbackend.config;

import org.example.emotionbackend.utils.JwtUtils;
import org.example.emotionbackend.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 请求 (跨域预检)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        
        // 前端通常会发 "Bearer token_string"，需要去掉前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token != null && jwtUtils.validateToken(token)) {
            // Token 有效，解析出 UserId 并存入上下文
            Long userId = jwtUtils.getUserIdFromToken(token);
            UserContext.setUserId(userId);
            return true;
        }

        // 验证失败
        response.setStatus(401);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write("{\"code\":401, \"message\":\"未登录或Token过期\"}");
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束，清理 ThreadLocal，防止内存泄漏
        UserContext.remove();
    }
}

