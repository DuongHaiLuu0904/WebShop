package com.ptit.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ptit.dao.CategoryDAO;
import com.ptit.entity.Category;
import com.ptit.service.CategoryService;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryDAO cdao;

    @Override
    public List<Category> findAll() {
        return cdao.findAll();
    }

    @Override
    public Category findById(String id) {
        return cdao.findById(id).get();
    }

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

    @Override
    public Category update(Category category) {
        return cdao.save(category);
    }

    @Override
    public void delete(String id) {
        cdao.deleteById(id);
    }

}
