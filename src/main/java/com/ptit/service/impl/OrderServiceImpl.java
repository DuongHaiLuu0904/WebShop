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
        
        // Handle customer mapping
        if (orderData.has("customer") && orderData.get("customer").has("username")) {
            String username = orderData.get("customer").get("username").asText();
            Customers customer = customerDAO.findById(username).orElse(null);
            order.setCustomer(customer);
        }
        
        dao.save(order);

        TypeReference<List<OrderDetail>> type = new TypeReference<List<OrderDetail>>() {};
        List<OrderDetail> details = mapper.convertValue(orderData.get("orderDetails"), type).stream()
                .peek(d -> d.setOrder(order)).collect(Collectors.toList());
        ddao.saveAll(details);
        return order;
    }

    @Override
    public Order findById(Long id) {
        return dao.findById(id).get();
    }

    @Override
    public List<Order> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        return dao.findByUsername(username);
    }
}
