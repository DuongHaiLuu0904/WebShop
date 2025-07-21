package com.ptit.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptit.dao.CustomerDAO;
import com.ptit.dao.OrderDAO;
import com.ptit.dao.OrderDetailDAO;
import com.ptit.dao.CartItemDAO;
import com.ptit.entity.Customers;
import com.ptit.entity.Order;
import com.ptit.entity.OrderDetail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    OrderDAO dao;

    @Autowired
    OrderDetailDAO ddao;
    
    @Autowired
    CustomerDAO customerDAO;
    
    @Autowired
    CartItemDAO cartItemDAO;
    
    @Autowired
    CartCacheService cartCacheService;
    
    @CacheEvict(value = "orders", allEntries = true)
    public Order create(JsonNode orderData) {
        ObjectMapper mapper = new ObjectMapper();
        
        // Tạo đơn hàng trước, chưa có khách hàng
        Order order = new Order();
        order.setAddress(orderData.get("address").asText());
        order.setCreateDate(new java.util.Date());
        
        // Thiết lập trạng thái thanh toán
        if (orderData.has("paymentStatus")) {
            order.setPaymentStatus(orderData.get("paymentStatus").asText());
        } else {
            order.setPaymentStatus("PENDING");
        }
        
        // Thiết lập ID giao dịch VNPay nếu được cung cấp
        if (orderData.has("vnpayTransactionId") && !orderData.get("vnpayTransactionId").isNull()) {
            order.setVnpayTransactionId(orderData.get("vnpayTransactionId").asText());
        }
        
        // Thiết lập tổng số tiền nếu được cung cấp
        if (orderData.has("totalAmount") && !orderData.get("totalAmount").isNull()) {
            order.setTotalAmount(orderData.get("totalAmount").asDouble());
        }
        
        Integer customerId = null;
        
        // Xử lý ánh xạ khách hàng
        if (orderData.has("customer")) {
            JsonNode customerNode = orderData.get("customer");
            Customers customer = null;
            
            // Thử tìm khách hàng theo tên đăng nhập trước
            if (customerNode.has("username")) {
                String username = customerNode.get("username").asText();
                customer = customerDAO.findByUsername(username);
            }
            // Nếu không tìm thấy theo tên đăng nhập, thử theo ID
            else if (customerNode.has("id")) {
                Integer id = customerNode.get("id").asInt();
                customerId = id;
                customer = customerDAO.findById(id).orElse(null);
            }
            
            if (customer != null) {
                order.setCustomer(customer);
                customerId = customer.getId();
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
        
        // Tính toán và cập nhật tổng số tiền nếu chưa được cung cấp
        if (order.getTotalAmount() == null) {
            double totalAmount = details.stream()
                    .mapToDouble(detail -> detail.getPrice() * detail.getQuantity())
                    .sum();
            order.setTotalAmount(totalAmount);
            dao.save(order);
        }
        
        // Xóa giỏ hàng sau khi tạo đơn hàng thành công
        if (customerId != null) {
            try {
                System.out.println("Đang xóa giỏ hàng cho khách hàng: " + customerId);
                cartItemDAO.deleteByCustomerId(customerId);
                cartCacheService.removeCartFromCache(customerId);
                cartCacheService.saveCartCount(customerId, 0);
                System.out.println("Xóa giỏ hàng thành công cho khách hàng: " + customerId);
            } catch (Exception e) {
                System.err.println("Lỗi khi xóa giỏ hàng cho khách hàng " + customerId + ": " + e.getMessage());
                // Không ném ngoại lệ vì đơn hàng đã được tạo thành công
            }
        }
        
        return order;
    }    
    
    @Cacheable(value = "orders", key = "#id", unless="#result == null")
    public Order findById(Long id) {
        Order order = dao.findById(id).orElse(null);
        if (order != null && order.getTotalAmount() == null && order.getOrderDetails() != null) {
            // Tự động tính tổng số tiền nếu giá trị null
            double totalAmount = order.getOrderDetails().stream()
                    .mapToDouble(detail -> detail.getPrice() * detail.getQuantity())
                    .sum();
            order.setTotalAmount(totalAmount);
            dao.save(order);
        }
        return order;
    }
    
    @Cacheable(value = "orders", key = "'user:' + #username")
    public List<Order> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        return dao.findByUsername(username);
    }

    @CacheEvict(value = "orders", key = "#orderId")
    public Order updatePaymentStatus(Long orderId, String paymentStatus) {
        Order order = dao.findById(orderId).orElse(null);
        if (order != null) {
            order.setPaymentStatus(paymentStatus);
            // Xóa cache người dùng nếu có thông tin khách hàng
            if (order.getCustomer() != null && order.getCustomer().getUsername() != null) {
                clearUserOrdersCache(order.getCustomer().getUsername());
            }
            return dao.save(order);
        }
        return null;
    }
    
    @CacheEvict(value = "orders", key = "'user:' + #username")
    private void clearUserOrdersCache(String username) {
        // Phương thức để xóa cache đơn hàng của người dùng cụ thể
    }

    public Order updateVnpayTransactionId(Long orderId, String vnpayTransactionId) {
        Order order = dao.findById(orderId).orElse(null);
        if (order != null) {
            order.setVnpayTransactionId(vnpayTransactionId);
            dao.save(order);
        }
        return order;
    }    
    
    public Order updateTotalAmount(Long orderId, Double totalAmount) {
        Order order = dao.findById(orderId).orElse(null);
        if (order != null) {
            order.setTotalAmount(totalAmount);
            dao.save(order);
        }
        return order;
    }

    public List<Order> findByPaymentStatus(String paymentStatus) {
        return dao.findByPaymentStatus(paymentStatus);
    }

    public Order findByVnpayTransactionId(String vnpayTransactionId) {
        return dao.findByVnpayTransactionId(vnpayTransactionId);
    }    
    
    public List<Order> findByUsernameAndPaymentStatus(String username, String paymentStatus) {
        return dao.findByUsernameAndPaymentStatus(username, paymentStatus);
    }

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
