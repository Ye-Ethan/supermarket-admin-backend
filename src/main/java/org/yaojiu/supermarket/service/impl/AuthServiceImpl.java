package org.yaojiu.supermarket.service.impl;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaojiu.supermarket.entity.UserDTO;
import org.yaojiu.supermarket.exception.InvalidDataException;
import org.yaojiu.supermarket.exception.NeedLoginException;
import org.yaojiu.supermarket.service.AuthService;
import org.yaojiu.supermarket.service.UserService;
import org.yaojiu.supermarket.utils.JwtUtils;
import org.yaojiu.supermarket.utils.RedisUtils;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtils redisUtil;

    // 常量定义
    private static final long REFRESH_EXPIRE = 7 * 24 * 60 * 60; // Refresh Token 7天
    private static final String REDIS_REFRESH_PREFIX = "auth:refresh:";
    private static final String REDIS_BLACKLIST_PREFIX = "auth:blacklist:";

    /**
     * 登录业务
     */
    public Map<String, String> login(UserDTO userDTO) {
        // 1. 生成 Access Token (JWT)
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDTO.getUsername());
        claims.put("role", userDTO.getUserType());
        String accessToken = jwtUtils.generateToken(claims, String.valueOf(userDTO.getId()));
        // 2. 生成原始 UUID (用于存 Redis)
        String rawUuid = UUID.randomUUID().toString();

        // 3. 存 Redis (Key 是 userId，方便以后管理员踢人)
        // Key: auth:refresh:1001  Value: rawUuid
        redisUtil.set("auth:refresh:" + userDTO.getId(), rawUuid, 7, TimeUnit.DAYS);

        // 4. 【重点】生成发给前端的“组合 Token”
        // 格式：userId:rawUuid -> 转 Base64
        String compositeString = userDTO.getId() + ":" + rawUuid;
        String finalRefreshToken = Base64.getEncoder().encodeToString(compositeString.getBytes());

        // 5. 返回
        Map<String, String> map = new HashMap<>();
        map.put("access_token", accessToken);
        map.put("refresh_token", finalRefreshToken); // 前端拿到的是这一串 Base64
        return map;
    }

    /**
     * 注销业务
     * @param token 前端传来的 Access Token
     */
    public void logout(String token) {
        // 1. 解析 Token
        Claims claims = jwtUtils.parseToken(token);
        String userId = claims.getSubject();

        String jti = claims.getId();

        // 2. 计算剩余过期时间
        long exp = claims.getExpiration().getTime();
        long now = System.currentTimeMillis();
        long ttl = (exp - now) / 1000; // 剩余秒数

        // 3. 【关键】如果 Access Token 还没过期，加入黑名单
        if (ttl > 0) {
            // Key: auth:blacklist:xxxx-xxxx  Value: 1  TTL: 剩余时间
            redisUtil.set(REDIS_BLACKLIST_PREFIX + jti, "1", ttl, TimeUnit.SECONDS);
        }

        // 4. 【关键】删除 Redis 里的 Refresh Token (彻底踢出)
        redisUtil.delete(REDIS_REFRESH_PREFIX + userId);
    }

    /**
     * 刷新 Token 业务
     */
    public Map<String, String> refreshToken(String clientRefreshToken) {

        String decodedStr;
        try {
            decodedStr = new String(Base64.getDecoder().decode(clientRefreshToken));
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException();
        }

        // 格式应该是 "userId:uuid"
        String[] parts = decodedStr.split(":");
        if (parts.length != 2) {
            throw new InvalidDataException();
        }

        String userIdStr = parts[0];
        String clientUuid = parts[1];

        String redisKey = REDIS_REFRESH_PREFIX + userIdStr;
        String redisUuid = redisUtil.get(redisKey);

        // 3. 校验：Redis里有没有？UUID对不对？
        if (redisUuid == null || !redisUuid.equals(clientUuid)) {
            throw new NeedLoginException();// 这里抛出 403
        }

        // 4. 验证通过！生成新的 Access Token
        // 注意：这里通常需要查库获取最新的 role/permission
        // User user = userService.getById(Long.valueOf(userIdStr));

        Map<String, Object> newClaims = new HashMap<>();
        // 用查出来的 user 信息
        UserDTO userById = userService.getUserById(Integer.parseInt(userIdStr));
        if (userById == null) {
            throw new NeedLoginException();
        }
        newClaims.put("username", userById.getUsername());
        newClaims.put("role", userById.getUserType());

        String newAccessToken = jwtUtils.generateToken(newClaims, userIdStr);

        Map<String, String> res = new HashMap<>();
        res.put("access_token", newAccessToken);
        return res;
    }
}
