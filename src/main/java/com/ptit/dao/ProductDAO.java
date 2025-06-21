package com.ptit.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ptit.entity.Product;

import java.util.List;

public interface ProductDAO extends JpaRepository<Product, Integer> {
    @Query("SELECT p FROM Product p WHERE p.category.id=?1")
    List<Product> findByCategoryId(String cid);

    Page<Product> findByCategoryId(String cid, Pageable pageable);

    @Query(value = "SELECT count(p.id) FROM Products p", nativeQuery = true)
    Integer countAllProduct();

    // Search methods cho tìm kiếm không phân biệt dấu
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> findByNameIgnoringAccents(@Param("keyword") String keyword);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.category.id = :categoryId")
    List<Product> findByNameIgnoringAccentsAndCategoryId(@Param("keyword") String keyword, @Param("categoryId") String categoryId);
    
}
