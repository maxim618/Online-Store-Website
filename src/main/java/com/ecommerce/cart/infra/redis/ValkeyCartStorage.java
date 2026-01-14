package com.ecommerce.cart.infra.redis;

import com.ecommerce.cart.domain.CartStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "cart.storage", havingValue = "redis")
public class ValkeyCartStorage implements CartStorage {

    private static final Duration CART_TTL = Duration.ofDays(30);

    private final StringRedisTemplate redis;

    private String key(long userId) {
        return "cart:" + userId;
    }

    private void touchTtl(String k) {
        redis.expire(k, CART_TTL);
    }

    @Override
    public void addItem(long userId, long productId, int deltaQty) {
        if (deltaQty <= 0) throw new IllegalArgumentException("deltaQty must be > 0");

        String k = key(userId);
        redis.opsForHash().increment(k, String.valueOf(productId), deltaQty);
        touchTtl(k);
    }

    @Override
    public void setItemQuantity(long userId, long productId, int qty) {
        String k = key(userId);
        String field = String.valueOf(productId);

        if (qty <= 0) {
            redis.opsForHash().delete(k, field);
            touchTtl(k);
            return;
        }

        redis.opsForHash().put(k, field, String.valueOf(qty));
        touchTtl(k);
    }

    @Override
    public void removeItem(long userId, long productId) {
        String k = key(userId);
        redis.opsForHash().delete(k, String.valueOf(productId));
        touchTtl(k);
    }

    @Override
    public void clearCart(long userId) {
        redis.delete(key(userId));
    }

    @Override
    public Map<Long, Integer> getItems(long userId) {
        String k = key(userId);

        Map<Object, Object> raw = redis.opsForHash().entries(k);
        if (raw == null || raw.isEmpty()) return Map.of();

        Map<Long, Integer> result = new HashMap<>(raw.size());
        for (Map.Entry<Object, Object> e : raw.entrySet()) {
            long pid = Long.parseLong(e.getKey().toString());
            int qty = Integer.parseInt(e.getValue().toString());
            if (qty > 0) result.put(pid, qty);
        }

        touchTtl(k);
        return result;
    }
}
