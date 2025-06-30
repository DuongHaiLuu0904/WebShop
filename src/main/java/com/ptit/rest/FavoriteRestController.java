package com.ptit.rest;

import com.ptit.entity.Customers;
import com.ptit.entity.Favorite;
import com.ptit.entity.Product;
import com.ptit.service.FavoriteService;
import com.ptit.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/rest/favorites")
public class FavoriteRestController {

    @Autowired
    private FavoriteService favoriteService;
    
    @Autowired
    private CustomerService customerService;

    // Thêm sản phẩm vào danh sách yêu thích
    @PostMapping("/add/{productId}")
    public ResponseEntity<?> addToFavorites(@PathVariable Integer productId) {
        try {
            // Lấy thông tin người dùng hiện tại
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập để sử dụng tính năng này");
                return ResponseEntity.ok(response);
            }

            // Lấy username từ principal và tìm customer
            String username = auth.getName();
            Customers customer = customerService.findByUsername(username);
            if (customer == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Không tìm thấy thông tin người dùng");
                return ResponseEntity.ok(response);
            }
            
            Integer customerId = customer.getId();

            Favorite favorite = favoriteService.addToFavorites(customerId, productId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã thêm vào danh sách yêu thích");
            response.put("favorite", favorite);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // Xóa sản phẩm khỏi danh sách yêu thích
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<?> removeFromFavorites(@PathVariable Integer productId) {
        try {
            // Lấy thông tin người dùng hiện tại
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập để sử dụng tính năng này");
                return ResponseEntity.ok(response);
            }

            // Lấy username từ principal và tìm customer
            String username = auth.getName();
            Customers customer = customerService.findByUsername(username);
            if (customer == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Không tìm thấy thông tin người dùng");
                return ResponseEntity.ok(response);
            }
            
            Integer customerId = customer.getId();

            favoriteService.removeFromFavorites(customerId, productId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã xóa khỏi danh sách yêu thích");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // Lấy danh sách sản phẩm yêu thích
    @GetMapping()
    public ResponseEntity<?> getFavorites() {
        try {
            // Lấy thông tin người dùng hiện tại
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập để sử dụng tính năng này");
                return ResponseEntity.ok(response);
            }

            // Lấy username từ principal và tìm customer
            String username = auth.getName();
            
            Customers customer = customerService.findByUsername(username);
            if (customer == null) {
                System.out.println("Customer not found for username: " + username);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Không tìm thấy thông tin người dùng");
                return ResponseEntity.ok(response);
            }
            
            Integer customerId = customer.getId();

            List<Product> favorites = favoriteService.getFavoriteProductsByCustomerId(customerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("favorites", favorites);
            response.put("count", favorites.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    // Debug endpoint - xóa sau khi fix xong
    @GetMapping("/debug")
    public ResponseEntity<?> debugFavorites() {
        try {
            // Get current user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                return ResponseEntity.ok("Not authenticated");
            }
            
            String username = auth.getName();
            Customers customer = customerService.findByUsername(username);
            if (customer == null) {
                return ResponseEntity.ok("Customer not found for username: " + username);
            }
            
            Integer customerId = customer.getId();
            
            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("username", username);
            debugInfo.put("customerId", customerId);
            
            return ResponseEntity.ok(debugInfo);
        } catch (Exception e) {
            return ResponseEntity.ok("Error: " + e.getMessage());
        }
    }

    // Kiểm tra xem sản phẩm đã được yêu thích chưa
    @GetMapping("/check/{productId}")
    public ResponseEntity<?> checkIsFavorite(@PathVariable Integer productId) {
        try {
            // Lấy thông tin người dùng hiện tại
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("isFavorite", false);
                return ResponseEntity.ok(response);
            }

            // Lấy username từ principal và tìm customer
            String username = auth.getName();
            Customers customer = customerService.findByUsername(username);
            if (customer == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("isFavorite", false);
                return ResponseEntity.ok(response);
            }
            
            Integer customerId = customer.getId();

            boolean isFavorite = favoriteService.isFavorite(customerId, productId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("isFavorite", isFavorite);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // Toggle favorite (thêm nếu chưa có, xóa nếu đã có)
    @PostMapping("/toggle/{productId}")
    public ResponseEntity<?> toggleFavorite(@PathVariable Integer productId) {
        try {
            // Lấy thông tin người dùng hiện tại
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập để sử dụng tính năng này");
                return ResponseEntity.ok(response);
            }

            // Lấy username từ principal và tìm customer
            String username = auth.getName();
            Customers customer = customerService.findByUsername(username);
            if (customer == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Không tìm thấy thông tin người dùng");
                return ResponseEntity.ok(response);
            }
            
            Integer customerId = customer.getId();

            boolean currentlyFavorite = favoriteService.isFavorite(customerId, productId);
            
            Map<String, Object> response = new HashMap<>();
            
            if (currentlyFavorite) {
                favoriteService.removeFromFavorites(customerId, productId);
                response.put("success", true);
                response.put("message", "Đã xóa khỏi danh sách yêu thích");
                response.put("isFavorite", false);
            } else {
                favoriteService.addToFavorites(customerId, productId);
                response.put("success", true);
                response.put("message", "Đã thêm vào danh sách yêu thích");
                response.put("isFavorite", true);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}
