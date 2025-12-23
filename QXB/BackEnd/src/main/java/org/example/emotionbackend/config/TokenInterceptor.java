package org.example.emotionbackend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.emotionbackend.utils.JwtUtils;
import org.example.emotionbackend.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 允许 OPTIONS（跨域预检请求）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String uri = request.getRequestURI();
        String token = request.getHeader("Authorization");

        // 处理 Bearer 前缀并尝试解析 Token
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 如果有有效 Token，解析并设置 UserContext
        if (token != null && !token.isEmpty() && jwtUtils.validateToken(token)) {
            Long userId = jwtUtils.getUserIdFromToken(token);
            UserContext.setUserId(userId);
        }

        // 完全公开的接口（不需要登录）- 简化逻辑
        if (uri.startsWith("/article/") ||
                uri.startsWith("/user/login") ||
                uri.startsWith("/user/register") ||
                uri.startsWith("/test/papers") ||
                uri.startsWith("/test/paper/")) {
            return true; // 放行
        }

        // 需要登录的接口
        if (UserContext.getUserId() == null) {
            writeError(response, "缺少Token，请先登录");
            return false;
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        UserContext.remove(); // 必须清除避免内存泄漏
    }

    private void writeError(HttpServletResponse response, String msg) throws Exception {
        response.setStatus(401);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write("{\"code\":401,\"message\":\"" + msg + "\"}");
    }
}