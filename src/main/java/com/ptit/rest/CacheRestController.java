package com.ptit.rest;

import com.ptit.service.RedisCacheMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@CrossOrigin("*")
@RestController
@RequestMapping("/rest/cache")
public class CacheRestController {

    @Autowired
    private RedisCacheMonitorService cacheMonitorService;

    // Lấy thống kê cache
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        Map<String, Object> stats = cacheMonitorService.getCacheStats();
        return ResponseEntity.ok(stats);
    }

    // Test kết nối Redis
    @GetMapping("/test-connection")
    public ResponseEntity<Map<String, Object>> testConnection() {
        boolean isConnected = cacheMonitorService.testRedisConnection();
        Map<String, Object> result = Map.of(
            "connected", isConnected,
            "message", isConnected ? "Redis connection successful" : "Redis connection failed"
        );
        return ResponseEntity.ok(result);
    }

    // Lấy tất cả cache keys
    @GetMapping("/keys")
    public ResponseEntity<Set<String>> getAllKeys() {
        Set<String> keys = cacheMonitorService.getAllKeys();
        return ResponseEntity.ok(keys);
    }

    // Lấy thông tin chi tiết về một key
    @GetMapping("/key/{keyName}")
    public ResponseEntity<Map<String, Object>> getKeyInfo(@PathVariable String keyName) {
        Map<String, Object> keyInfo = cacheMonitorService.getKeyInfo(keyName);
        return ResponseEntity.ok(keyInfo);
    }

    // Xóa tất cả cache
    @DeleteMapping("/clear-all")
    public ResponseEntity<Map<String, Object>> clearAllCache() {
        boolean success = cacheMonitorService.clearAllCache();
        Map<String, Object> result = Map.of(
            "success", success,
            "message", success ? "All cache cleared successfully" : "Failed to clear cache"
        );
        return ResponseEntity.ok(result);
    }

    // Xóa cache theo pattern
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearCacheByPattern(@RequestParam String pattern) {
        boolean success = cacheMonitorService.clearCacheByPattern(pattern);
        Map<String, Object> result = Map.of(
            "success", success,
            "message", success ? "Cache cleared for pattern: " + pattern : "Failed to clear cache for pattern: " + pattern
        );
        return ResponseEntity.ok(result);
    }

    // Xóa cache bị corrupted
    @DeleteMapping("/clear-corrupted")
    public ResponseEntity<Map<String, Object>> clearCorruptedCache() {
        boolean success = cacheMonitorService.clearCorruptedCache();
        Map<String, Object> result = Map.of(
            "success", success,
            "message", success ? "Corrupted cache cleared successfully" : "Failed to clear corrupted cache"
        );
        return ResponseEntity.ok(result);
    }

    // Warm up cache
    @PostMapping("/warm-up")
    public ResponseEntity<Map<String, Object>> warmUpCache() {
        Map<String, Object> result = cacheMonitorService.warmUpCache();
        return ResponseEntity.ok(result);
    }
}
