package com.ptit.debug;

import com.ptit.dao.ProductDAO;
import com.ptit.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DebugController {

    @Autowired
    ProductDAO productDAO;

    @GetMapping("/debug/products")
    public List<Product> getProducts() {
        return productDAO.findAll();
    }
}
