package com.ptit.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.ptit.dao.OrderDAO;
import com.ptit.entity.Order;
import com.ptit.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/rest/orders")
public class OrderRestController {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderDAO orderDAO;

    @GetMapping
    public List<Order> getAll() {
        return orderDAO.findAll();
    }    
    
    @PostMapping
    public Order create(@RequestBody JsonNode orderData) {
        return orderService.create(orderData);
    }

    @PostMapping("/create-from-payment")
    public ResponseEntity<Order> createFromPayment(@RequestBody JsonNode orderData) {
        try {
            Order order = orderService.create(orderData);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable Long id) {
        Order order = orderService.findById(id);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{username}")
    public List<Order> getByUsername(@PathVariable String username) {
        return orderService.findByUsername(username);
    }

    @PutMapping("/{id}/payment-status")
    public ResponseEntity<Order> updatePaymentStatus(@PathVariable Long id, @RequestBody JsonNode data) {
        String paymentStatus = data.get("paymentStatus").asText();
        Order updatedOrder = orderService.updatePaymentStatus(id, paymentStatus);
        return updatedOrder != null ? ResponseEntity.ok(updatedOrder) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/vnpay-transaction")
    public ResponseEntity<Order> updateVnpayTransactionId(@PathVariable Long id, @RequestBody JsonNode data) {
        String vnpayTransactionId = data.get("vnpayTransactionId").asText();
        Order updatedOrder = orderService.updateVnpayTransactionId(id, vnpayTransactionId);
        return updatedOrder != null ? ResponseEntity.ok(updatedOrder) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/total-amount")
    public ResponseEntity<Order> updateTotalAmount(@PathVariable Long id, @RequestBody JsonNode data) {
        Double totalAmount = data.get("totalAmount").asDouble();
        Order updatedOrder = orderService.updateTotalAmount(id, totalAmount);
        return updatedOrder != null ? ResponseEntity.ok(updatedOrder) : ResponseEntity.notFound().build();
    }

    @GetMapping("/payment-status/{status}")
    public List<Order> getByPaymentStatus(@PathVariable String status) {
        return orderService.findByPaymentStatus(status);
    }

    @GetMapping("/vnpay-transaction/{transactionId}")
    public ResponseEntity<Order> getByVnpayTransactionId(@PathVariable String transactionId) {
        Order order = orderService.findByVnpayTransactionId(transactionId);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{username}/payment-status/{status}")
    public List<Order> getByUsernameAndPaymentStatus(@PathVariable String username, @PathVariable String status) {
        return orderService.findByUsernameAndPaymentStatus(username, status);
    }

    @PostMapping("/{id}/calculate-total")
    public ResponseEntity<Order> calculateTotalAmount(@PathVariable Long id) {
        Order updatedOrder = orderService.calculateAndUpdateTotalAmount(id);
        return updatedOrder != null ? ResponseEntity.ok(updatedOrder) : ResponseEntity.notFound().build();
    }
}
