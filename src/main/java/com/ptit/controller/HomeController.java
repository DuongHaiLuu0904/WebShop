package com.ptit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ptit.dao.ProductDAO;
import com.ptit.entity.Category;
import com.ptit.entity.Product;
import com.ptit.service.CategoryService;
import com.ptit.service.ProductService;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    ProductDAO pdao;

    @Autowired
    ProductService productService;

    @Autowired
    CategoryService categoryService;

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

    @RequestMapping("search")
    public String search(Model model, 
                        @RequestParam(name = "keyword", required = false) String keyword,
                        @RequestParam(name = "cid", required = false) String cid) {
        
        // Lấy danh sách categories cho menu
        List<Category> cates = categoryService.findAll();
        model.addAttribute("cates", cates);
        
        // Tìm kiếm sản phẩm
        List<Product> list;
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (cid != null) {
                // Tìm kiếm theo keyword và category (không phân biệt dấu)
                list = productService.findByNameIgnoringAccentsAndCategoryId(keyword.trim(), cid);
            } else {
                // Tìm kiếm chỉ theo keyword (không phân biệt dấu)
                list = productService.findByNameIgnoringAccents(keyword.trim());
            }
            model.addAttribute("keyword", keyword);
        } else if (cid != null) {
            // Chỉ lọc theo category
            list = productService.findByCategoryId(cid);
        } else {
            // Không có điều kiện tìm kiếm, hiển thị tất cả
            list = productService.findAll();
        }
        
        model.addAttribute("items", list);
        model.addAttribute("selectedCid", cid);
        
        return "search-results";
    }
}
