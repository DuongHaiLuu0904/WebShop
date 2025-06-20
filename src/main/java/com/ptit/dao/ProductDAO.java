package com.ptit.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ptit.entity.Product;

import java.util.List;

public interface ProductDAO extends JpaRepository<Product, Integer> {
    @Query("SELECT p FROM Product p WHERE p.category.id=?1")
    List<Product> findByCategoryId(String cid);

    Page<Product> findByCategoryId(String cid, Pageable pageable);

    @Query(value = "SELECT count(p.id) FROM Products p", nativeQuery = true)
    Integer countAllProduct();

    
}
