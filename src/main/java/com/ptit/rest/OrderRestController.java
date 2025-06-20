package com.ptit.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.ptit.dao.OrderDAO;
import com.ptit.entity.Order;
import com.ptit.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
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
}
