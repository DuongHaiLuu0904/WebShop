package com.ptit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ptit.entity.Product;
import com.ptit.entity.Category;
import com.ptit.service.ProductService;
import com.ptit.service.CategoryService;

import java.util.List;

@Controller
public class ProductController {
    @Autowired
    ProductService productService;
    
    @Autowired
    CategoryService categoryService;

    @RequestMapping("/product/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id) {
        // Load categories for menu
        List<Category> cates = categoryService.findAll();
        model.addAttribute("cates", cates);
        
        Product item = productService.findById(id);
        model.addAttribute("item", item);
        return "product/detail";
    }

    @RequestMapping("/product/list")
    public String list(Model model) {
        // Load categories for menu
        List<Category> cates = categoryService.findAll();
        model.addAttribute("cates", cates);
        
        model.addAttribute("items", productService.findAll());
        return "product/list";
    }
}
