package com.ptit.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ptit.entity.Authority;
import com.ptit.entity.Customers;

import java.util.List;

public interface AuthorityDAO extends JpaRepository<Authority, Integer> {
    @Query("SELECT DISTINCT a FROM Authority a WHERE a.customer IN ?1")
    List<Authority> authoritiesOf(List<Customers> customers);
}
