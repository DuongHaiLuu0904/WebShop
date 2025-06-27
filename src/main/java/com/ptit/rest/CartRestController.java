package com.ptit.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ptit.entity.CartItem;
import com.ptit.entity.CartItemId;
import com.ptit.entity.Product;
import com.ptit.dao.CartItemDAO;
import com.ptit.dao.ProductDAO;
import com.ptit.dao.CustomerDAO;
import com.ptit.entity.Customers;

import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/rest/cart")
public class CartRestController {

    @Autowired
    CartItemDAO cartItemDAO;

    @Autowired
    ProductDAO productDAO;   
    
    @Autowired
    CustomerDAO customerDAO;
    
    // Helper method to resolve customer ID from string (could be username or ID)
    private Integer resolveCustomerId(String customerIdOrUsername) {
        try {
            // Try to parse as Integer first
            return Integer.parseInt(customerIdOrUsername);
        } catch (NumberFormatException e) {
            // If not a number, treat as username and lookup customer
            Customers customer = customerDAO.findByUsername(customerIdOrUsername);
            if (customer != null) {
                return customer.getId();
            }
            throw new RuntimeException("Customer not found: " + customerIdOrUsername);
        }
    }

    // Lấy tất cả items trong giỏ hàng của user
    @GetMapping("/{customerIdOrUsername}")
    public List<CartItem> getCartItems(@PathVariable("customerIdOrUsername") String customerIdOrUsername) {
        Integer customerId = resolveCustomerId(customerIdOrUsername);
        return cartItemDAO.findByCustomerId(customerId);
    }

    // Thêm sản phẩm vào giỏ hàng
    @PostMapping("/{customerIdOrUsername}/add/{productId}")
    public ResponseEntity<CartItem> addToCart(@PathVariable("customerIdOrUsername") String customerIdOrUsername,
            @PathVariable("productId") Integer productId) {
        try {
            Integer customerId = resolveCustomerId(customerIdOrUsername);
            // Kiểm tra sản phẩm có tồn tại không
            Optional<Product> productOpt = productDAO.findById(productId);
            if (!productOpt.isPresent()) {
                return ResponseEntity.badRequest().build();
            }

            Product product = productOpt.get();

            // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
            CartItem existingItem = cartItemDAO.findByCustomerIdAndProductId(customerId, productId);

            if (existingItem != null) {
                // Nếu đã có thì tăng số lượng
                existingItem.setQuantity(existingItem.getQuantity() + 1);
                return ResponseEntity.ok(cartItemDAO.save(existingItem));
            } else {
                // Nếu chưa có thì tạo mới
                CartItem newItem = new CartItem(customerId, productId, 1, product.getPrice());
                return ResponseEntity.ok(cartItemDAO.save(newItem));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }    
    
    // Cập nhật số lượng sản phẩm trong giỏ hàng
    @PutMapping("/{customerIdOrUsername}/update/{productId}")
    public ResponseEntity<CartItem> updateQuantity(@PathVariable("customerIdOrUsername") String customerIdOrUsername,
            @PathVariable("productId") Integer productId,
            @RequestParam("quantity") Integer quantity) {
        try {
            Integer customerId = resolveCustomerId(customerIdOrUsername);
            CartItem item = cartItemDAO.findByCustomerIdAndProductId(customerId, productId);
            if (item != null) {
                if (quantity > 0) {
                    item.setQuantity(quantity);
                    return ResponseEntity.ok(cartItemDAO.save(item));
                } else {
                    // Nếu quantity <= 0 thì xóa item
                    cartItemDAO.deleteById(new CartItemId(customerId, productId));
                    return ResponseEntity.ok().build();
                }
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Xóa sản phẩm khỏi giỏ hàng
    @DeleteMapping("/{customerIdOrUsername}/remove/{productId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable("customerIdOrUsername") String customerIdOrUsername,
            @PathVariable("productId") Integer productId) {
        try {
            Integer customerId = resolveCustomerId(customerIdOrUsername);
            cartItemDAO.deleteById(new CartItemId(customerId, productId));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Xóa tất cả sản phẩm trong giỏ hàng
    @DeleteMapping("/{customerIdOrUsername}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable("customerIdOrUsername") String customerIdOrUsername) {
        try {
            Integer customerId = resolveCustomerId(customerIdOrUsername);
            cartItemDAO.deleteByCustomerId(customerId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Lấy tổng số lượng sản phẩm trong giỏ hàng
    @GetMapping("/{customerIdOrUsername}/count")
    public ResponseEntity<Integer> getCartCount(@PathVariable("customerIdOrUsername") String customerIdOrUsername) {
        try {
            Integer customerId = resolveCustomerId(customerIdOrUsername);
            Integer count = cartItemDAO.getTotalQuantityByCustomerId(customerId);
            return ResponseEntity.ok(count != null ? count : 0);
        } catch (Exception e) {
            return ResponseEntity.ok(0);
        }
    }

    // Lấy tổng tiền trong giỏ hàng
    @GetMapping("/{customerIdOrUsername}/amount")
    public ResponseEntity<Double> getCartAmount(@PathVariable("customerIdOrUsername") String customerIdOrUsername) {
        try {
            Integer customerId = resolveCustomerId(customerIdOrUsername);
            Double amount = cartItemDAO.getTotalAmountByCustomerId(customerId);
            return ResponseEntity.ok(amount != null ? amount : 0.0);
        } catch (Exception e) {
            return ResponseEntity.ok(0.0);
        }
    }
}
