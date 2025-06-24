package com.ptit.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ptit.entity.Customers;

import java.util.List;

public interface CustomerDAO extends JpaRepository<Customers, Integer> {
    @Query("SELECT DISTINCT ar.customer FROM Authority ar WHERE ar.role.id IN('DIRE','STAF')")
    List<Customers> getAdministrators();

    @Query("SELECT a FROM Customers a WHERE a.username =?1 and a.password=?2")
    Customers getCustomer(String username, String password);

    // Tìm theo username
    @Query("SELECT a FROM Customers a WHERE a.username=?1")
    Customers findByUsername(String username);

    // Phuc vu viec gui mail
    @Query("SELECT a FROM Customers a WHERE a.email=?1")
    public Customers findByEmail(String email);

    @Query("SELECT a FROM Customers a WHERE a.token=?1")
    public Customers findByToken(String token);

	@Query(value = "SELECT count(a.id) FROM Customers a", nativeQuery = true)
	Integer countAllCustomer();
}
