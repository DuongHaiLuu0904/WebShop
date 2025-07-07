package com.ptit.dao;

import com.ptit.entity.Favorite;
import com.ptit.entity.FavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FavoriteDAO extends JpaRepository<Favorite, FavoriteId> {
    
    // Tìm tất cả sản phẩm yêu thích của một khách hàng với eager loading
    @Query("SELECT f FROM Favorite f JOIN FETCH f.product WHERE f.customerId = :customerId")
    List<Favorite> findByCustomerId(@Param("customerId") Integer customerId);
    
    // Kiểm tra xem sản phẩm đã được yêu thích bởi khách hàng chưa
    @Query("SELECT f FROM Favorite f WHERE f.customerId = :customerId AND f.productId = :productId")
    Favorite findByCustomerIdAndProductId(@Param("customerId") Integer customerId, @Param("productId") Integer productId);
    
    // Xóa sản phẩm khỏi danh sách yêu thích
    @Modifying
    @Transactional
    @Query("DELETE FROM Favorite f WHERE f.customerId = :customerId AND f.productId = :productId")
    void deleteByCustomerIdAndProductId(@Param("customerId") Integer customerId, @Param("productId") Integer productId);
    
    // Đếm số sản phẩm yêu thích của khách hàng
    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.customerId = :customerId")
    Long countByCustomerId(@Param("customerId") Integer customerId);
}
