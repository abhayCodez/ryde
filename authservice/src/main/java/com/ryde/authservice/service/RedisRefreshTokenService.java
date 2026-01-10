package com.ryde.authservice.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RedisRefreshTokenService {

    private static final long TTL_DAYS = 1;
    private final RedisTemplate<String, String> redis;

    public RedisRefreshTokenService(@Qualifier("redisTemplate") RedisTemplate<String, String> redis) {
        this.redis = redis;
    }

    public String create(Long userId){
        String token = UUID.randomUUID().toString();
        String refreshKey = refreshKey(token);
        String sessionKey = sessionKey(userId);
        redis.opsForValue().set(
                refreshKey,
                userId.toString(),
                TTL_DAYS,
                TimeUnit.DAYS
        );
        redis.opsForSet().add(sessionKey, token);
        return token;
    }

    public Long verify(String token){
        String userId = redis.opsForValue().get(refreshKey(token));
        if(userId == null){
            throw new RuntimeException("Invalid or Expired refresh token");
        }
        return Long.parseLong(userId);
    }

    public boolean delete(String token) {
        String refreshKey = refreshKey(token);
        String userId = redis.opsForValue().get(refreshKey);
        if (userId == null) {
            return false;
        }
        Boolean deleted = redis.delete(refreshKey);
        redis.opsForSet().remove(sessionKey(Long.parseLong(userId)), token);
        return Boolean.TRUE.equals(deleted);
    }

    public void deleteAllForUser(Long userId){
        String sessionKey = sessionKey(userId);
        Set<String> tokens = redis.opsForSet().members(sessionKey);
        if(tokens != null){
            for (String token: tokens){
                redis.delete(refreshKey(token));
            }
        }
        redis.delete(sessionKey);
    }

    public String sessionKey(Long userId) {
        return "user_sessions:" + userId;
    }

    public String refreshKey(String token) {
        return "refresh:" + token;
    }
}