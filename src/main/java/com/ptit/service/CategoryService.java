package com.ptit.service;

import java.util.List;

import com.ptit.entity.Category;

public interface CategoryService {

    List<Category> findAll();

    Category findById(String id);

    Category create(Category category);

    Category update(Category category);

    void delete(String id);

}
