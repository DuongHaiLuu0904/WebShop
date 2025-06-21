package com.ptit.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ptit.service.OrderService;

@Controller
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    HttpServletRequest request;

    @RequestMapping("/cart/view")
    public String cart() {
        return "cart/view";
    }

    @RequestMapping("/cart/checkout")
    public String checkout() {
        if (!(request.isUserInRole("DIRE") || request.isUserInRole("STAF") || request.isUserInRole("CUST"))) {
            return "redirect:/auth/login/form";
        }
        return "cart/checkout";
    }

    @RequestMapping("/order/list")
    public String list(Model model, HttpServletRequest request) {
        String username = request.getRemoteUser();
        if (username == null) {
            return "redirect:/auth/login/form";
        }
        model.addAttribute("orders", orderService.findByUsername(username));
        return "order/list";
    }

    @RequestMapping("/order/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        model.addAttribute("order", orderService.findById(id));
        return "order/detail";
    }
}
