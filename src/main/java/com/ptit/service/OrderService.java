package com.ptit.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.ptit.entity.Order;

import java.util.List;

public interface OrderService {
    Order create(JsonNode orderData);

    Order findById(Long id);

    List<Order> findByUsername(String username);
    
    List<Order> findByPaymentStatus(String paymentStatus);
    
    Order findByVnpayTransactionId(String vnpayTransactionId);
    
    List<Order> findByUsernameAndPaymentStatus(String username, String paymentStatus);
    
    Order updatePaymentStatus(Long orderId, String paymentStatus);
    
    Order updateVnpayTransactionId(Long orderId, String vnpayTransactionId);
      Order updateTotalAmount(Long orderId, Double totalAmount);
    
    Order calculateAndUpdateTotalAmount(Long orderId);
}
