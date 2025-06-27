package com.ptit.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptit.dao.CustomerDAO;
import com.ptit.dao.OrderDAO;
import com.ptit.dao.OrderDetailDAO;
import com.ptit.entity.Customers;
import com.ptit.entity.Order;
import com.ptit.entity.OrderDetail;
import com.ptit.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderDAO dao;

    @Autowired
    OrderDetailDAO ddao;
    
    @Autowired
    CustomerDAO customerDAO;    
    
    @Override
    public Order create(JsonNode orderData) {
        ObjectMapper mapper = new ObjectMapper();
        
        // Create order without customer first
        Order order = new Order();
        order.setAddress(orderData.get("address").asText());
        order.setCreateDate(new java.util.Date());
        
        // Set payment status
        if (orderData.has("paymentStatus")) {
            order.setPaymentStatus(orderData.get("paymentStatus").asText());
        } else {
            order.setPaymentStatus("PENDING");
        }
        
        // Set VNPay transaction ID if provided
        if (orderData.has("vnpayTransactionId") && !orderData.get("vnpayTransactionId").isNull()) {
            order.setVnpayTransactionId(orderData.get("vnpayTransactionId").asText());
        }
        
        // Set total amount if provided
        if (orderData.has("totalAmount") && !orderData.get("totalAmount").isNull()) {
            order.setTotalAmount(orderData.get("totalAmount").asDouble());
        }
        // Handle customer mapping
        if (orderData.has("customer")) {
            JsonNode customerNode = orderData.get("customer");
            Customers customer = null;
            
            // Try to find customer by username first
            if (customerNode.has("username")) {
                String username = customerNode.get("username").asText();
                customer = customerDAO.findByUsername(username);
            }
            // If not found by username, try by ID
            else if (customerNode.has("id")) {
                Integer customerId = customerNode.get("id").asInt();
                customer = customerDAO.findById(customerId).orElse(null);
            }
            
            if (customer != null) {
                order.setCustomer(customer);
            } else {
                throw new RuntimeException("Customer not found in database");
            }
        } else {
            throw new RuntimeException("Customer information is required");
        }
          dao.save(order);

        TypeReference<List<OrderDetail>> type = new TypeReference<List<OrderDetail>>() {};
        List<OrderDetail> details = mapper.convertValue(orderData.get("orderDetails"), type).stream()
                .peek(d -> d.setOrder(order)).collect(Collectors.toList());
        ddao.saveAll(details);
        
        // Calculate and update total amount if not provided
        if (order.getTotalAmount() == null) {
            double totalAmount = details.stream()
                    .mapToDouble(detail -> detail.getPrice() * detail.getQuantity())
                    .sum();
            order.setTotalAmount(totalAmount);
            dao.save(order);
        }
        
        return order;
    }    
    
    @Override
    public Order findById(Long id) {
        Order order = dao.findById(id).orElse(null);
        if (order != null && order.getTotalAmount() == null && order.getOrderDetails() != null) {
            // Auto-calculate total amount if it's null
            double totalAmount = order.getOrderDetails().stream()
                    .mapToDouble(detail -> detail.getPrice() * detail.getQuantity())
                    .sum();
            order.setTotalAmount(totalAmount);
            dao.save(order);
        }
        return order;
    }
    
    @Override
    public List<Order> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        return dao.findByUsername(username);
    }

    @Override
    public Order updatePaymentStatus(Long orderId, String paymentStatus) {
        Order order = dao.findById(orderId).orElse(null);
        if (order != null) {
            order.setPaymentStatus(paymentStatus);
            dao.save(order);
        }
        return order;
    }

    @Override
    public Order updateVnpayTransactionId(Long orderId, String vnpayTransactionId) {
        Order order = dao.findById(orderId).orElse(null);
        if (order != null) {
            order.setVnpayTransactionId(vnpayTransactionId);
            dao.save(order);
        }
        return order;
    }    
    
    @Override
    public Order updateTotalAmount(Long orderId, Double totalAmount) {
        Order order = dao.findById(orderId).orElse(null);
        if (order != null) {
            order.setTotalAmount(totalAmount);
            dao.save(order);
        }
        return order;
    }

    @Override
    public List<Order> findByPaymentStatus(String paymentStatus) {
        return dao.findByPaymentStatus(paymentStatus);
    }

    @Override
    public Order findByVnpayTransactionId(String vnpayTransactionId) {
        return dao.findByVnpayTransactionId(vnpayTransactionId);
    }    
    
    @Override
    public List<Order> findByUsernameAndPaymentStatus(String username, String paymentStatus) {
        return dao.findByUsernameAndPaymentStatus(username, paymentStatus);
    }

    @Override
    public Order calculateAndUpdateTotalAmount(Long orderId) {
        Order order = dao.findById(orderId).orElse(null);
        if (order != null && order.getOrderDetails() != null) {
            double totalAmount = order.getOrderDetails().stream()
                    .mapToDouble(detail -> detail.getPrice() * detail.getQuantity())
                    .sum();
            order.setTotalAmount(totalAmount);
            dao.save(order);
        }
        return order;
    }
}
