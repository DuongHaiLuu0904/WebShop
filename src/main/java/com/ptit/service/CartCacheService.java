package com.ptit.service;

import com.ptit.entity.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Service
public class CartCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CART_KEY_PREFIX = "cart:";
    private static final Duration CART_TTL = Duration.ofHours(2);

    /**
     * Lưu giỏ hàng vào Redis cache
     */
    public void saveCartToCache(Integer customerId, List<CartItem> cartItems) {
        String key = CART_KEY_PREFIX + customerId;
        redisTemplate.opsForValue().set(key, cartItems, CART_TTL);
    }

    /**
     * Lấy giỏ hàng từ Redis cache
     */
    @SuppressWarnings("unchecked")
    public List<CartItem> getCartFromCache(Integer customerId) {
        String key = CART_KEY_PREFIX + customerId;
        return (List<CartItem>) redisTemplate.opsForValue().get(key);
    }

    /**
     * Xóa giỏ hàng khỏi Redis cache
     */
    public void removeCartFromCache(Integer customerId) {
        String key = CART_KEY_PREFIX + customerId;
        redisTemplate.delete(key);
    }

    /**
     * Cập nhật thời gian hết hạn của giỏ hàng
     */
    public void refreshCartExpiry(Integer customerId) {
        String key = CART_KEY_PREFIX + customerId;
        if (redisTemplate.hasKey(key)) {
            redisTemplate.expire(key, CART_TTL);
        }
    }

    /**
     * Lưu số lượng sản phẩm trong giỏ hàng
     */
    public void saveCartCount(Integer customerId, Integer count) {
        String key = CART_KEY_PREFIX + customerId + ":count";
        redisTemplate.opsForValue().set(key, count, CART_TTL);
    }

    /**
     * Lấy số lượng sản phẩm trong giỏ hàng
     */
    public Integer getCartCount(Integer customerId) {
        String key = CART_KEY_PREFIX + customerId + ":count";
        Object count = redisTemplate.opsForValue().get(key);
        return count != null ? (Integer) count : null;
    }

    /**
     * Lưu thông tin session của người dùng
     */
    public void saveUserSession(String sessionId, Object userInfo) {
        String key = "session:" + sessionId;
        redisTemplate.opsForValue().set(key, userInfo, Duration.ofHours(1));
    }

    /**
     * Lấy thông tin session của người dùng
     */
    public Object getUserSession(String sessionId) {
        String key = "session:" + sessionId;
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Xóa session của người dùng
     */
    public void removeUserSession(String sessionId) {
        String key = "session:" + sessionId;
        redisTemplate.delete(key);
    }

    /**
     * Lưu wishlist vào cache
     */
    public void saveWishlistToCache(Integer customerId, Set<Integer> productIds) {
        String key = "wishlist:" + customerId;
        redisTemplate.opsForValue().set(key, productIds, Duration.ofHours(1));
    }

    /**
     * Lấy wishlist từ cache
     */
    @SuppressWarnings("unchecked")
    public Set<Integer> getWishlistFromCache(Integer customerId) {
        String key = "wishlist:" + customerId;
        return (Set<Integer>) redisTemplate.opsForValue().get(key);
    }

    /**
     * Xóa wishlist khỏi cache
     */
    public void removeWishlistFromCache(Integer customerId) {
        String key = "wishlist:" + customerId;
        redisTemplate.delete(key);
    }

    /**
     * Cache cho thống kê realtime
     */
    public void saveStats(String statKey, Object value) {
        String key = "stats:" + statKey;
        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(5));
    }

    /**
     * Lấy thống kê từ cache
     */
    public Object getStats(String statKey) {
        String key = "stats:" + statKey;
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Increment counter cho thống kê
     */
    public Long incrementCounter(String counterKey) {
        String key = "counter:" + counterKey;
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * Lưu thông tin tạm thời trong quá trình checkout
     */
    public void saveCheckoutSession(String sessionId, Object checkoutData) {
        String key = "checkout:" + sessionId;
        redisTemplate.opsForValue().set(key, checkoutData, Duration.ofMinutes(30));
    }

    /**
     * Lấy thông tin checkout session
     */
    public Object getCheckoutSession(String sessionId) {
        String key = "checkout:" + sessionId;
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Xóa checkout session
     */
    public void removeCheckoutSession(String sessionId) {
        String key = "checkout:" + sessionId;
        redisTemplate.delete(key);
    }
}
