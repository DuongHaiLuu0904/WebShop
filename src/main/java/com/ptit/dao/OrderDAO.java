package com.ptit.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ptit.entity.Order;

import java.util.List;

public interface OrderDAO extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.customer.username=?1")
    List<Order> findByUsername(String username);
}
