package com.ptit.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ptit.dao.RoleDAO;
import com.ptit.entity.Role;
import com.ptit.service.RoleService;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    RoleDAO dao;

    public List<Role> findAll() {
        return dao.findAll();
    }
}
