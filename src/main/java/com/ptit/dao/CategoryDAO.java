package com.ptit.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ptit.entity.Category;

public interface CategoryDAO extends JpaRepository<Category, String> {
    @Query("SELECT MAX(CAST(c.id AS int)) FROM Category c")
    Integer findMaxId();
}
