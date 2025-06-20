package com.ptit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ptit.entity.Product;
import com.ptit.service.ProductService;

@Controller
public class ProductController {
    @Autowired
    ProductService productService;

    @RequestMapping("/product/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id) {
        Product item = productService.findById(id);
        model.addAttribute("item", item);
        return "product/detail";
    }

    @RequestMapping("/product/list")
    public String list(Model model) {
        model.addAttribute("items", productService.findAll());
        return "product/list";
    }
}
