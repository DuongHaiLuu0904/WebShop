package com.ptit.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ptit.dao.CategoryDAO;
import com.ptit.dao.ProductDAO;
import com.ptit.entity.Product;
import com.ptit.service.ProductService;
import com.ptit.util.StringUtils;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductDAO pdao;

    @Autowired
    CategoryDAO cdao;

    @Override
    public List<Product> findAll() {
        return pdao.findAll();
    }

    @Override
    public Product findById(Integer id) {
        return pdao.findById(id).get();
    }

    @Override
    public List<Product> findByCategoryId(String cid) {
        return pdao.findByCategoryId(cid);
    }

    public Product create(Product product) {
        // Đảm bảo category là entity managed
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            product.setCategory(cdao.findById(product.getCategory().getId()).orElse(null));
        }
        // Đảm bảo quantity không null
        if (product.getQuantity() == null) {
            product.setQuantity(0);
        }
        return pdao.save(product);
    }

    @Override
    public Product update(Product product) {
        return pdao.save(product);
    }

    @Override
    public void delete(Integer id) {
        pdao.deleteById(id);
    }

    public Page<Product> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return pdao.findAll(pageable);
    }

    public Page<Product> findByCategoryId(String cid, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return pdao.findByCategoryId(cid, pageable);
    }

    @Override
    public List<Product> findByNameIgnoringAccents(String keyword) {
        // Xử lý keyword trước khi search
        String processedKeyword = StringUtils.removeAccents(keyword);
        return pdao.findByNameIgnoringAccents(processedKeyword);
    }

    @Override
    public List<Product> findByNameIgnoringAccentsAndCategoryId(String keyword, String categoryId) {
        String processedKeyword = StringUtils.removeAccents(keyword);
        return pdao.findByNameIgnoringAccentsAndCategoryId(processedKeyword, categoryId);
    }

}
