package com.ptit.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ptit.entity.OrderDetail;

public interface OrderDetailDAO extends JpaRepository<OrderDetail, Long> {
	@Query(value = "SELECT sum(o.price * o.quantity) FROM OrderDetails o", nativeQuery = true)
	String countSumOrder();
}
