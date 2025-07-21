package com.ptit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ptit.dao.RoleDAO;
import com.ptit.entity.Role;

import java.util.List;

@Service
public class RoleService {
    @Autowired
    RoleDAO dao;

    public List<Role> findAll() {
        return dao.findAll();
    }
}
