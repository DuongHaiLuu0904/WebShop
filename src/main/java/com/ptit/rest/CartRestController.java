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
    ProductDAO productDAO;

    // Lấy tất cả items trong giỏ hàng của user
    @GetMapping("/{username}")
    public List<CartItem> getCartItems(@PathVariable("username") String username) {
        return cartItemDAO.findByUsername(username);
    }

    // Thêm sản phẩm vào giỏ hàng
    @PostMapping("/{username}/add/{productId}")
    public ResponseEntity<CartItem> addToCart(@PathVariable("username") String username,
            @PathVariable("productId") Integer productId) {
        try {
            // Kiểm tra sản phẩm có tồn tại không
            Optional<Product> productOpt = productDAO.findById(productId);
            if (!productOpt.isPresent()) {
                return ResponseEntity.badRequest().build();
            }

            Product product = productOpt.get();

            // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
            CartItem existingItem = cartItemDAO.findByUsernameAndProductId(username, productId);

            if (existingItem != null) {
                // Nếu đã có thì tăng số lượng
                existingItem.setQuantity(existingItem.getQuantity() + 1);
                return ResponseEntity.ok(cartItemDAO.save(existingItem));
            } else {
                // Nếu chưa có thì tạo mới
                CartItem newItem = new CartItem(username, productId, 1, product.getPrice());
                return ResponseEntity.ok(cartItemDAO.save(newItem));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    @PutMapping("/{username}/update/{productId}")
    public ResponseEntity<CartItem> updateQuantity(@PathVariable("username") String username,
            @PathVariable("productId") Integer productId,
            @RequestParam("quantity") Integer quantity) {
        try {
            CartItem item = cartItemDAO.findByUsernameAndProductId(username, productId);
            if (item != null) {
                if (quantity > 0) {
                    item.setQuantity(quantity);
                    return ResponseEntity.ok(cartItemDAO.save(item));
                } else {
                    // Nếu quantity <= 0 thì xóa item
                    cartItemDAO.deleteById(new CartItemId(username, productId));
                    return ResponseEntity.ok().build();
                }
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Xóa sản phẩm khỏi giỏ hàng
    @DeleteMapping("/{username}/remove/{productId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable("username") String username,
            @PathVariable("productId") Integer productId) {
        try {
            cartItemDAO.deleteById(new CartItemId(username, productId));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Xóa tất cả sản phẩm trong giỏ hàng
    @DeleteMapping("/{username}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable("username") String username) {
        try {
            cartItemDAO.deleteByUsername(username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Lấy tổng số lượng sản phẩm trong giỏ hàng
    @GetMapping("/{username}/count")
    public ResponseEntity<Integer> getCartCount(@PathVariable("username") String username) {
        try {
            Integer count = cartItemDAO.getTotalQuantityByUsername(username);
            return ResponseEntity.ok(count != null ? count : 0);
        } catch (Exception e) {
            return ResponseEntity.ok(0);
        }
    }

    // Lấy tổng tiền trong giỏ hàng
    @GetMapping("/{username}/amount")
    public ResponseEntity<Double> getCartAmount(@PathVariable("username") String username) {
        try {
            Double amount = cartItemDAO.getTotalAmountByUsername(username);
            return ResponseEntity.ok(amount != null ? amount : 0.0);
        } catch (Exception e) {
            return ResponseEntity.ok(0.0);
        }
    }
}
