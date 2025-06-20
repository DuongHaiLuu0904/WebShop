package com.ptit.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ptit.entity.Role;

public interface RoleDAO extends JpaRepository<Role, String> {
    
}
