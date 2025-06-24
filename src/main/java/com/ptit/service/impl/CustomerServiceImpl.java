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
    BCryptPasswordEncoder pe;    @Override
    public Customers findById(Integer id) {
        return adao.findById(id).orElse(null);
    }

    @Override
    public Customers findByUsername(String username) {
        return adao.findByUsername(username);
    }

    @Override
    public List<Customers> findAll() {
        return adao.findAll();
    }

    @Override
    public List<Customers> getAdministrators() {
        return adao.getAdministrators();
    }    @Override
    public Customers create(Customers customer) {
        // Đảm bảo token không bao giờ null khi tạo mới
        if (customer.getToken() == null || customer.getToken().trim().isEmpty()) {
            customer.setToken("token");
        }
        return adao.save(customer);
    }@Override
    public Customers update(Customers customer) {
        // Lấy thông tin customer hiện tại từ database bằng ID
        Customers existingCustomer = adao.findById(customer.getId()).orElse(null);
        if (existingCustomer != null) {
            // Cập nhật các trường từ customer mới vào existingCustomer
            existingCustomer.setUsername(customer.getUsername());
            existingCustomer.setPassword(customer.getPassword());
            existingCustomer.setFullname(customer.getFullname());
            existingCustomer.setEmail(customer.getEmail());
            if (customer.getPhoto() != null && !customer.getPhoto().trim().isEmpty()) {
                existingCustomer.setPhoto(customer.getPhoto());
            }
            
            // Giữ lại token cũ nếu token mới null hoặc empty
            if (customer.getToken() == null || customer.getToken().trim().isEmpty()) {
                // Không thay đổi token cũ
            } else {
                existingCustomer.setToken(customer.getToken());
            }
            
            // Đảm bảo token không bao giờ null
            if (existingCustomer.getToken() == null) {
                existingCustomer.setToken("token");
            }
            
            return adao.save(existingCustomer);
        }
        return adao.save(customer);
    }

    @Override
    public void delete(Integer id) {
        adao.deleteById(id);
    }

    @Override
    public Customers saveCustomer(Customers customer) {
        // Đảm bảo token không bao giờ null
        if (customer.getToken() == null) {
            customer.setToken("token");
        }
        return adao.save(customer);
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
