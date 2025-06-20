package com.ptit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ptit.dao.ProductDAO;
import com.ptit.entity.Product;
import com.ptit.service.ProductService;

import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    ProductDAO pdao;

    @Autowired
    ProductService productService;

    @RequestMapping({"/", "/index"})
    public String home(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam("cid") Optional<String> cid, Model model) {
        if (cid.isPresent()) {
            Page<Product> productPage = productService.findByCategoryId(cid.get(), page, size);
            model.addAttribute("items", productPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", productPage.getTotalPages());
        } else {
            Page<Product> productPage = productService.getAllProducts(page, size);
            model.addAttribute("items", productPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", productPage.getTotalPages());
        }
        return "index";
    }

    @RequestMapping({"/admin", "/admin/index"})
    public String admin(Model model) {
        return "redirect:/admin/index.html";
    }

    @RequestMapping("about")
    public String about() {
        return "about";
    }

    @RequestMapping("contact")
    public String contact() {
        return "contact";
    }
}
