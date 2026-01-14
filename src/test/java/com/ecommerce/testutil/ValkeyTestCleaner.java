package com.ecommerce.testutil;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValkeyTestCleaner {

    private final StringRedisTemplate redis;

    public void clearAll() {
        var cf = redis.getConnectionFactory();
        if (cf == null) {
            throw new IllegalStateException("RedisConnectionFactory is null");
        }

        try (var conn = cf.getConnection()) {
            conn.serverCommands().flushAll();
        }
    }
}
