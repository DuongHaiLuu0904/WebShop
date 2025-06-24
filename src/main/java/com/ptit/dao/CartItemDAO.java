package com.ptit.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ptit.entity.CartItem;
import com.ptit.entity.CartItemId;

import java.util.List;

public interface CartItemDAO extends JpaRepository<CartItem, CartItemId> {
    
    @Query("SELECT c FROM CartItem c WHERE c.customerId = ?1")
    List<CartItem> findByCustomerId(Integer customerId);
    
    @Query("SELECT c FROM CartItem c WHERE c.customerId = ?1 AND c.productId = ?2")
    CartItem findByCustomerIdAndProductId(Integer customerId, Integer productId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.customerId = ?1")
    void deleteByCustomerId(Integer customerId);
    
    @Query("SELECT SUM(c.quantity) FROM CartItem c WHERE c.customerId = ?1")
    Integer getTotalQuantityByCustomerId(Integer customerId);
    
    @Query("SELECT SUM(c.quantity * c.price) FROM CartItem c WHERE c.customerId = ?1")
    Double getTotalAmountByCustomerId(Integer customerId);
}
