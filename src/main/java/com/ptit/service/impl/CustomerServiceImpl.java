package com.ptit.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import com.ptit.dao.CustomerDAO;
import com.ptit.entity.Customers;
import com.ptit.service.CustomerService;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerDAO adao;

    @Autowired
    @Lazy
    BCryptPasswordEncoder pe;

    @Override
    public Customers findById(String username) {
        return adao.findById(username).get();
    }

    @Override
    public List<Customers> findAll() {
        return adao.findAll();
    }

    @Override
    public List<Customers> getAdministrators() {
        return adao.getAdministrators();
    }

    @Override
    public Customers create(Customers customer) {
        return adao.save(customer);
    }

    @Override
    public Customers update(Customers customer) {
        return adao.save(customer);
    }

    @Override
    public void delete(String username) {
        adao.deleteById(username);
    }

    @Override
    public void loginFromOAuth2(OAuth2AuthenticationToken oauth2) {
        // String fullname = oauth2.getPrincipal().getAttribute("name");
        String email = oauth2.getPrincipal().getAttribute("email");
        String password = Long.toHexString(System.currentTimeMillis());

        UserDetails user = User.withUsername(email).password(pe.encode(password)).roles("CUST").build();
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Override
    public void updateToken(String token, String email) throws Exception {
        Customers entity = adao.findByEmail(email);
        if (entity != null) {
            entity.setToken(token);
            adao.save(entity);
        } else {
            throw new Exception("Cannot find any customer with email: " + email);
        }
    }

    @Override
    public Customers getByToken(String token) {
        return adao.findByToken(token);
    }

    @Override
    public void updatePassword(Customers entity, String newPassword) {
        entity.setPassword(newPassword);
        entity.setToken("token");
        adao.save(entity);
    }

    @Override
    public void changePassword(Customers entity, String newPassword) {
        entity.setPassword(newPassword);
        adao.save(entity);
    }
}
