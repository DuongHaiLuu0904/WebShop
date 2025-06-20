package com.ptit.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ptit.entity.CartItem;
import com.ptit.entity.CartItemId;

import java.util.List;

public interface CartItemDAO extends JpaRepository<CartItem, CartItemId> {
    
    @Query("SELECT c FROM CartItem c WHERE c.username = ?1")
    List<CartItem> findByUsername(String username);
    
    @Query("SELECT c FROM CartItem c WHERE c.username = ?1 AND c.productId = ?2")
    CartItem findByUsernameAndProductId(String username, Integer productId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.username = ?1")
    void deleteByUsername(String username);
    
    @Query("SELECT SUM(c.quantity) FROM CartItem c WHERE c.username = ?1")
    Integer getTotalQuantityByUsername(String username);
    
    @Query("SELECT SUM(c.quantity * c.price) FROM CartItem c WHERE c.username = ?1")
    Double getTotalAmountByUsername(String username);
}
