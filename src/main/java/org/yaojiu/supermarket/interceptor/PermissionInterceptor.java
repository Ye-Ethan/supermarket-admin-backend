package org.yaojiu.supermarket.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.yaojiu.supermarket.entity.Result;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.yaojiu.supermarket.exception.NeedLoginException;
import org.yaojiu.supermarket.exception.TokenInvalidException;
import org.yaojiu.supermarket.utils.JwtUtils;
import org.yaojiu.supermarket.utils.RedisUtils;
import org.yaojiu.supermarket.utils.UserContext;

import java.io.PrintWriter;

@Slf4j
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisUtils redisUtils;

    private static final String REDIS_BLACKLIST_PREFIX = "auth:blacklist:";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 如果是 OPTIONS 请求（跨域预检），直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 2. 获取 Token
        String token = request.getHeader("Authorization");
        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            // 这里可以抛出自定义异常，被全局异常处理捕获
            throw new NeedLoginException();
        }
        token = token.substring(7);
        log.info("token:{}", token);

        // 3. 校验 Token 有效性
        if (!jwtUtils.validateToken(token)) {
            throw new TokenInvalidException();
        }

        // 4. 解析 Token
        Claims claims = jwtUtils.parseToken(token);
        String jti = claims.getId();

        // 5. 【黑名单校验】
        if (redisUtils.hasKey(REDIS_BLACKLIST_PREFIX + jti)) {
            throw new TokenInvalidException();
        }

        String userIdStr = claims.getSubject();
        UserContext.setUserId(Long.valueOf(userIdStr));

        return true; // 放行
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束，务必清理 ThreadLocal，防止内存泄漏（线程池复用导致数据串号）
        UserContext.clear();
    }
}
