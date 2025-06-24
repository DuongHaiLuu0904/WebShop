package com.ptit.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ptit.entity.CartItem;
import com.ptit.entity.CartItemId;
import com.ptit.entity.Product;
import com.ptit.dao.CartItemDAO;
import com.ptit.dao.ProductDAO;

import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/rest/cart")
public class CartRestController {

    @Autowired
    CartItemDAO cartItemDAO;

    @Autowired
    ProductDAO productDAO;    // Lấy tất cả items trong giỏ hàng của user
    @GetMapping("/{customerId}")
    public List<CartItem> getCartItems(@PathVariable("customerId") Integer customerId) {
        return cartItemDAO.findByCustomerId(customerId);
    }

    // Thêm sản phẩm vào giỏ hàng
    @PostMapping("/{customerId}/add/{productId}")
    public ResponseEntity<CartItem> addToCart(@PathVariable("customerId") Integer customerId,
            @PathVariable("productId") Integer productId) {
        try {
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
    }    // Cập nhật số lượng sản phẩm trong giỏ hàng
    @PutMapping("/{customerId}/update/{productId}")
    public ResponseEntity<CartItem> updateQuantity(@PathVariable("customerId") Integer customerId,
            @PathVariable("productId") Integer productId,
            @RequestParam("quantity") Integer quantity) {
        try {
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
    @DeleteMapping("/{customerId}/remove/{productId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable("customerId") Integer customerId,
            @PathVariable("productId") Integer productId) {
        try {
            cartItemDAO.deleteById(new CartItemId(customerId, productId));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Xóa tất cả sản phẩm trong giỏ hàng
    @DeleteMapping("/{customerId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable("customerId") Integer customerId) {
        try {
            cartItemDAO.deleteByCustomerId(customerId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Lấy tổng số lượng sản phẩm trong giỏ hàng
    @GetMapping("/{customerId}/count")
    public ResponseEntity<Integer> getCartCount(@PathVariable("customerId") Integer customerId) {
        try {
            Integer count = cartItemDAO.getTotalQuantityByCustomerId(customerId);
            return ResponseEntity.ok(count != null ? count : 0);
        } catch (Exception e) {
            return ResponseEntity.ok(0);
        }
    }

    // Lấy tổng tiền trong giỏ hàng
    @GetMapping("/{customerId}/amount")
    public ResponseEntity<Double> getCartAmount(@PathVariable("customerId") Integer customerId) {
        try {
            Double amount = cartItemDAO.getTotalAmountByCustomerId(customerId);
            return ResponseEntity.ok(amount != null ? amount : 0.0);
        } catch (Exception e) {
            return ResponseEntity.ok(0.0);
        }
    }
}
