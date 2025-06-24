package com.ptit.service;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import com.ptit.entity.Customers;

import java.util.List;

public interface CustomerService {
    Customers findById(Integer id);

    Customers findByUsername(String username);

    List<Customers> findAll();

    List<Customers> getAdministrators();

    Customers create(Customers customers);

    Customers update(Customers customers);

    Customers saveCustomer(Customers customer);

    void delete(Integer id);

    void loginFromOAuth2(OAuth2AuthenticationToken oauth2);

    void updateToken(String token, String email) throws Exception;

    Customers getByToken(String token);

    void updatePassword(Customers entity, String newPassword);

    void changePassword(Customers entity, String newPassword);
}
