package com.ptit.service;

import com.ptit.entity.Favorite;
import com.ptit.entity.Product;

import java.util.List;

public interface FavoriteService {
    
    // Thêm sản phẩm vào danh sách yêu thích
    Favorite addToFavorites(Integer customerId, Integer productId);
    
    // Xóa sản phẩm khỏi danh sách yêu thích
    void removeFromFavorites(Integer customerId, Integer productId);
    
    // Lấy danh sách sản phẩm yêu thích của khách hàng
    List<Favorite> getFavoritesByCustomerId(Integer customerId);
    
    // Lấy danh sách sản phẩm yêu thích (chỉ products) của khách hàng
    List<Product> getFavoriteProductsByCustomerId(Integer customerId);
    
    // Kiểm tra xem sản phẩm đã được yêu thích chưa
    boolean isFavorite(Integer customerId, Integer productId);
    
    // Đếm số sản phẩm yêu thích của khách hàng
    Long countFavoritesByCustomerId(Integer customerId);
}
