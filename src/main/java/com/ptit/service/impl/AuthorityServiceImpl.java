package com.ptit.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ptit.dao.AuthorityDAO;
import com.ptit.dao.CustomerDAO;
import com.ptit.entity.Authority;
import com.ptit.entity.Customers;
import com.ptit.service.AuthorityService;

import java.util.List;

@Service
public class AuthorityServiceImpl implements AuthorityService {
    @Autowired
    AuthorityDAO dao;
    @Autowired
    CustomerDAO acdao;

    @Override
    public List<Authority> findAuthoritiesOfAdministrators() {
        List<Customers> customers = acdao.getAdministrators();
        return dao.authoritiesOf(customers);
    }

    @Override
    public List<Authority> findAll() {
        return dao.findAll();
    }

    @Override
    public Authority create(Authority auth) {
        return dao.save(auth);
    }

    @Override
    public void delete(Integer id) {
        dao.deleteById(id);
    }

}
