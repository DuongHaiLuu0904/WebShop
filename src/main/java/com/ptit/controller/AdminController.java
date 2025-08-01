package com.ptit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @GetMapping({"", "/", "/index"})
    public String adminRedirect() {
        return "redirect:/admin/index.html";
    }
}
