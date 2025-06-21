package com.ptit.service;

import org.springframework.data.domain.Page;

import com.ptit.entity.Product;

import java.util.List;

public interface ProductService {
    List<Product> findAll();

    Product findById(Integer id);

    List<Product> findByCategoryId(String cid);

    Product create(Product product);

    Product update(Product product);

    void delete(Integer id);

    Page<Product> getAllProducts(int page, int size);

    Page<Product> findByCategoryId(String cid, int page, int size);

    // Search methods cho tìm kiếm không phân biệt dấu
    List<Product> findByNameIgnoringAccents(String keyword);
    
    List<Product> findByNameIgnoringAccentsAndCategoryId(String keyword, String categoryId);

}
