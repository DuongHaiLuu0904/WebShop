package com.ptit.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.ptit.service.OrderService;
import com.ptit.service.CategoryService;
import com.ptit.entity.Category;

import java.util.List;

@Controller
public class OrderController {    
    @Autowired
    OrderService orderService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    HttpServletRequest request;    
    
    @RequestMapping("/cart/view")
    public String cart(Model model) {
        // Load categories for menu
        List<Category> cates = categoryService.findAll();
        model.addAttribute("cates", cates);
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
    }    @RequestMapping("/order/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        model.addAttribute("order", orderService.findById(id));
        return "order/detail";
    }

    @PostMapping("/order/{id}/payment-status")
    @ResponseBody
    public ResponseEntity<?> updatePaymentStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        try {
            orderService.updatePaymentStatus(id, status);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating payment status: " + e.getMessage());
        }
    }

    @PostMapping("/order/{id}/vnpay-transaction")
    @ResponseBody
    public ResponseEntity<?> updateVnpayTransactionId(@PathVariable("id") Long id, @RequestParam("transactionId") String transactionId) {
        try {
            orderService.updateVnpayTransactionId(id, transactionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating VNPay transaction ID: " + e.getMessage());
        }
    }

    @PostMapping("/order/{id}/total-amount")
    @ResponseBody
    public ResponseEntity<?> updateTotalAmount(@PathVariable("id") Long id, @RequestParam("amount") Double amount) {
        try {
            orderService.updateTotalAmount(id, amount);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating total amount: " + e.getMessage());
        }
    }
}
