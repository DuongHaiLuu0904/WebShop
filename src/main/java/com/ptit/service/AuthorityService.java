package com.ptit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ptit.dao.AuthorityDAO;
import com.ptit.dao.CustomerDAO;
import com.ptit.entity.Authority;
import com.ptit.entity.Customers;

import java.util.List;

@Service
public class AuthorityService {
    @Autowired
    AuthorityDAO dao;
    @Autowired
    CustomerDAO acdao;

    public List<Authority> findAuthoritiesOfAdministrators() {
        List<Customers> customers = acdao.getAdministrators();
        return dao.authoritiesOf(customers);
    }

    public List<Authority> findAll() {
        return dao.findAll();
    }

    public Authority create(Authority auth) {
        return dao.save(auth);
    }

    public void delete(Integer id) {
        dao.deleteById(id);
    }
}
