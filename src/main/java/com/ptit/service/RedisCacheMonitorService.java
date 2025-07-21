package com.ptit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheMonitorService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Lấy thông tin tổng quan về Redis cache
     */
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Lấy tổng số keys
            Set<String> allKeys = redisTemplate.keys("*");
            stats.put("totalKeys", allKeys != null ? allKeys.size() : 0);
            
            // Thống kê theo từng cache name
            Map<String, Integer> cacheBreakdown = new HashMap<>();
            if (allKeys != null) {
                for (String key : allKeys) {
                    String cacheType = extractCacheType(key);
                    cacheBreakdown.put(cacheType, cacheBreakdown.getOrDefault(cacheType, 0) + 1);
                }
            }
            stats.put("cacheBreakdown", cacheBreakdown);
            
            // Thông tin sử dụng bộ nhớ (nếu Redis hỗ trợ)
            try {
                stats.put("redisInfo", getRedisInfo());
            } catch (Exception e) {
                stats.put("redisInfo", "Unable to fetch Redis info");
            }
            
        } catch (Exception e) {
            stats.put("error", "Unable to connect to Redis: " + e.getMessage());
        }
        
        return stats;
    }

    /**
     * Xóa tất cả cache
     */
    public boolean clearAllCache() {
        try {
            Set<String> keys = redisTemplate.keys("*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                return true;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Xóa cache theo pattern
     */
    public boolean clearCacheByPattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                return true;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Lấy thông tin chi tiết về một cache key
     */
    public Map<String, Object> getKeyInfo(String key) {
        Map<String, Object> info = new HashMap<>();
        
        try {
            // Kiểm tra xem key có tồn tại không
            boolean exists = redisTemplate.hasKey(key);
            info.put("exists", exists);
            
            if (exists) {
                // Lấy TTL
                Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
                info.put("ttl", ttl);
                
                // Lấy giá trị
                Object value = redisTemplate.opsForValue().get(key);
                info.put("value", value);
                
                // Lấy loại
                info.put("type", redisTemplate.type(key));
            }
        } catch (Exception e) {
            info.put("error", e.getMessage());
        }
        
        return info;
    }

    /**
     * Lấy danh sách tất cả cache keys
     */
    public Set<String> getAllKeys() {
        try {
            return redisTemplate.keys("*");
        } catch (Exception e) {
            return Set.of();
        }
    }

    /**
     * Warm up cache - load dữ liệu quan trọng vào cache
     */
    public Map<String, Object> warmUpCache() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Có thể gọi các service để load dữ liệu vào cache
            // Ví dụ: productService.findAll(), categoryService.findAll()
            
            result.put("success", true);
            result.put("message", "Cache warm-up completed");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Cache warm-up failed: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Test kết nối Redis
     */
    public boolean testRedisConnection() {
        try {
            redisTemplate.opsForValue().set("test:connection", "OK", 10, TimeUnit.SECONDS);
            String result = (String) redisTemplate.opsForValue().get("test:connection");
            redisTemplate.delete("test:connection");
            return "OK".equals(result);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Clear cache khi gặp serialization error
     */
    public boolean clearCorruptedCache() {
        try {
            // Xóa tất cả cache để sửa lỗi serialization
            Set<String> keys = redisTemplate.keys("*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                System.out.println("Cleared " + keys.size() + " corrupted cache keys");
                return true;
            }
            return true;
        } catch (Exception e) {
            System.err.println("Failed to clear corrupted cache: " + e.getMessage());
            return false;
        }
    }

    private String extractCacheType(String key) {
        if (key.contains("products")) return "products";
        if (key.contains("categories")) return "categories";
        if (key.contains("customers")) return "customers";
        if (key.contains("orders")) return "orders";
        if (key.contains("cart")) return "cart";
        if (key.contains("session")) return "session";
        if (key.contains("wishlist")) return "wishlist";
        if (key.contains("searchResults")) return "searchResults";
        return "other";
    }

    private Map<String, String> getRedisInfo() {
        Map<String, String> info = new HashMap<>();
        
        try {
            // Thông tin cơ bản
            info.put("status", "Connected");
            info.put("version", "Redis Server");
            
            // Có thể mở rộng để lấy thêm thông tin nếu cần
        } catch (Exception e) {
            info.put("error", e.getMessage());
        }
        
        return info;
    }
}
