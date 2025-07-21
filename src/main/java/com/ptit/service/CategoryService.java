package com.ptit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import com.ptit.dao.CategoryDAO;
import com.ptit.entity.Category;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    CategoryDAO cdao;

    @Cacheable(value = "categories", key = "'all'")
    public List<Category> findAll() {
        return cdao.findAll();
    }

    @Cacheable(value = "categories", key = "#id", unless="#result == null")
    public Category findById(String id) {
        return cdao.findById(id).orElse(null);
    }

    @CacheEvict(value = {"categories", "products"}, allEntries = true)
    public Category create(Category category) {
        if (category.getName() == null || category.getName().isEmpty()) {
            throw new IllegalArgumentException("Name không được để trống");
        }
        if (category.getId() == null || category.getId().isEmpty()) {
            Integer maxId = cdao.findMaxId();
            int nextId = (maxId == null) ? 1 : maxId + 1;
            category.setId(String.valueOf(nextId));
        } else if (cdao.existsById(category.getId())) {
            throw new IllegalArgumentException("Id đã tồn tại");
        }
        if (category.getNote() == null) {
            category.setNote("");
        }
        if (category.getDescription() == null) {
            category.setDescription("");
        }
        return cdao.save(category);
    }

    @CachePut(value = "categories", key = "#category.id")
    @CacheEvict(value = {"categories", "products"}, key = "'all'")
    public Category update(Category category) {
        return cdao.save(category);
    }

    @CacheEvict(value = {"categories", "products"}, allEntries = true)
    public void delete(String id) {
        cdao.deleteById(id);
    }
}
