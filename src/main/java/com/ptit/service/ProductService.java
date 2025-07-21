package com.ptit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ptit.dao.CategoryDAO;
import com.ptit.dao.ProductDAO;
import com.ptit.entity.Product;
import com.ptit.util.StringUtils;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    ProductDAO pdao;

    @Autowired
    CategoryDAO cdao;

    @Cacheable(value = "products", key = "'all'")
    public List<Product> findAll() {
        return pdao.findAll();
    }

    @Cacheable(value = "products", key = "#id", unless="#result == null")
    public Product findById(Integer id) {
        return pdao.findById(id).orElse(null);
    }

    @Cacheable(value = "products", key = "'category:' + #cid")
    public List<Product> findByCategoryId(String cid) {
        return pdao.findByCategoryId(cid);
    }

    @CacheEvict(value = "products", allEntries = true)
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

    @CachePut(value = "products", key = "#product.id")
    @CacheEvict(value = "products", key = "'all'")
    public Product update(Product product) {
        return pdao.save(product);
    }

    @CacheEvict(value = "products", key = "#id")
    public void delete(Integer id) {
        pdao.deleteById(id);
        // Cũng xóa cache "all" khi xóa một sản phẩm
        evictAllProductsCache();
    }
    
    @CacheEvict(value = "products", key = "'all'")
    private void evictAllProductsCache() {
        // Phương thức này được sử dụng để xóa cache "all"
    }

    // Lưu ý: Các phương thức phân trang không được cache do vấn đề serialization với PageImpl
    public Page<Product> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return pdao.findAll(pageable);
    }

    // Lưu ý: Các phương thức phân trang không được cache do vấn đề serialization với PageImpl  
    public Page<Product> findByCategoryId(String cid, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return pdao.findByCategoryId(cid, pageable);
    }

    @Cacheable(value = "searchResults", key = "'keyword:' + #keyword")
    public List<Product> findByNameIgnoringAccents(String keyword) {
        // Xử lý keyword trước khi search
        String processedKeyword = StringUtils.removeAccents(keyword);
        return pdao.findByNameIgnoringAccents(processedKeyword);
    }

    @Cacheable(value = "searchResults", key = "'keyword:' + #keyword + ':category:' + #categoryId")
    public List<Product> findByNameIgnoringAccentsAndCategoryId(String keyword, String categoryId) {
        String processedKeyword = StringUtils.removeAccents(keyword);
        return pdao.findByNameIgnoringAccentsAndCategoryId(processedKeyword, categoryId);
    }
}
