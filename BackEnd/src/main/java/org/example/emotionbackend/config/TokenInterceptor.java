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

        // 允许无需登录的接口
        String uri = request.getRequestURI();
        if (uri.startsWith("/user/login") ||
                uri.startsWith("/user/register") ||
                uri.startsWith("/test")) {

            return true; // 放行
        }

        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            writeError(response, "缺少Token，请先登录");
            return false;
        }

        // 处理 Bearer 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!jwtUtils.validateToken(token)) {
            writeError(response, "Token无效或已过期");
            return false;
        }

        // 解析并写入线程上下文
        Long userId = jwtUtils.getUserIdFromToken(token);
        UserContext.setUserId(userId);

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
