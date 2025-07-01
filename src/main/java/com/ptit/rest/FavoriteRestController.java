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

    private ResponseEntity<?> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    private Customers getAuthenticatedCustomer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        String username = auth.getName();
        return customerService.findByUsername(username);
    }

    // Thêm sản phẩm vào danh sách yêu thích
    @PostMapping("/add/{productId}")
    public ResponseEntity<?> addToFavorites(@PathVariable Integer productId) {
        try {
            Customers customer = getAuthenticatedCustomer();
            if (customer == null) {
                return createErrorResponse("Bạn cần đăng nhập để sử dụng tính năng này");
            }

            Favorite favorite = favoriteService.addToFavorites(customer.getId(), productId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã thêm vào danh sách yêu thích");
            response.put("favorite", favorite);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Có lỗi xảy ra: " + e.getMessage());
        }
    }

    // Xóa sản phẩm khỏi danh sách yêu thích
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<?> removeFromFavorites(@PathVariable Integer productId) {
        try {
            Customers customer = getAuthenticatedCustomer();
            if (customer == null) {
                return createErrorResponse("Bạn cần đăng nhập để sử dụng tính năng này");
            }

            favoriteService.removeFromFavorites(customer.getId(), productId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã xóa khỏi danh sách yêu thích");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Có lỗi xảy ra: " + e.getMessage());
        }
    }

    // Lấy danh sách sản phẩm yêu thích
    @GetMapping()
    public ResponseEntity<?> getFavorites() {
        try {
            Customers customer = getAuthenticatedCustomer();
            if (customer == null) {
                return createErrorResponse("Bạn cần đăng nhập để sử dụng tính năng này");
            }

            List<Product> favorites = favoriteService.getFavoriteProductsByCustomerId(customer.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("favorites", favorites);
            response.put("count", favorites.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error in getFavorites: " + e.getMessage());
            return createErrorResponse("Có lỗi xảy ra: " + e.getMessage());
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
            Customers customer = getAuthenticatedCustomer();
            if (customer == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("isFavorite", false);
                return ResponseEntity.ok(response);
            }

            boolean isFavorite = favoriteService.isFavorite(customer.getId(), productId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("isFavorite", isFavorite);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Có lỗi xảy ra: " + e.getMessage());
        }
    }

    // Toggle favorite (thêm nếu chưa có, xóa nếu đã có)
    @PostMapping("/toggle/{productId}")
    public ResponseEntity<?> toggleFavorite(@PathVariable Integer productId) {
        try {
            Customers customer = getAuthenticatedCustomer();
            if (customer == null) {
                return createErrorResponse("Bạn cần đăng nhập để sử dụng tính năng này");
            }

            boolean currentlyFavorite = favoriteService.isFavorite(customer.getId(), productId);
            
            Map<String, Object> response = new HashMap<>();
            
            if (currentlyFavorite) {
                favoriteService.removeFromFavorites(customer.getId(), productId);
                response.put("success", true);
                response.put("message", "Đã xóa khỏi danh sách yêu thích");
                response.put("isFavorite", false);
            } else {
                favoriteService.addToFavorites(customer.getId(), productId);
                response.put("success", true);
                response.put("message", "Đã thêm vào danh sách yêu thích");
                response.put("isFavorite", true);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Có lỗi xảy ra: " + e.getMessage());
        }
    }
}
