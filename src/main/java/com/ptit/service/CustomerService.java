package com.ptit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
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
import com.ptit.dao.RoleDAO;
import com.ptit.entity.Customers;
import com.ptit.entity.Role;
import com.ptit.entity.Authority;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    CustomerDAO adao;

    @Autowired
    RoleDAO roleDAO;

    @Autowired
    AuthorityService authorityService;

    @Autowired
    @Lazy
    BCryptPasswordEncoder pe;    
    
    @PersistenceContext
    private EntityManager entityManager;    
    
    @Cacheable(value = "customers", key = "#id", unless="#result == null")
    public Customers findById(Integer id) {
        return adao.findById(id).orElse(null);
    }

    @Cacheable(value = "customers", key = "'username:' + #username", unless="#result == null")
    public Customers findByUsername(String username) {
        return adao.findByUsername(username);
    }
    
    @CacheEvict(value = "customers", key = "'username:' + #username")
    public void clearUserCache(String username) {
        // Method để clear cache cho specific user
    }
    
    @Cacheable(value = "customers", key = "'email:' + #email", unless="#result == null")
    public Customers findByEmail(String email) {
        return adao.findByEmail(email);
    }

    @Cacheable(value = "customers", key = "'all'")
    public List<Customers> findAll() {
        return adao.findAll();
    }

    @Cacheable(value = "customers", key = "'admins'")
    public List<Customers> getAdministrators() {
        return adao.getAdministrators();
    }    
    
    @CacheEvict(value = "customers", allEntries = true)
    public Customers create(Customers customer) {
        if (customer.getToken() == null || customer.getToken().trim().isEmpty()) {
            customer.setToken("token");
        }
        return adao.save(customer);
    }
    
    @CachePut(value = "customers", key = "#customer.id")
    @CacheEvict(value = "customers", allEntries = true)
    public Customers update(Customers customer) {
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
            
            if (existingCustomer.getToken() == null) {
                existingCustomer.setToken("token");
            }
            
            return adao.save(existingCustomer);
        }
        return adao.save(customer);
    }

    @CacheEvict(value = "customers", allEntries = true)
    public void delete(Integer id) {
        adao.deleteById(id);
    }

    public Customers saveCustomer(Customers customer) {
        // Đảm bảo token không bao giờ null
        if (customer.getToken() == null) {
            customer.setToken("token");
        }
        return adao.save(customer);
    }

    public void loginFromOAuth2(OAuth2AuthenticationToken oauth2) {
        String fullname = oauth2.getPrincipal().getAttribute("name");
        String email = oauth2.getPrincipal().getAttribute("email");
        String picture = oauth2.getPrincipal().getAttribute("picture");
        
        // Kiểm tra xem customer đã tồn tại chưa
        Customers existingCustomer = adao.findByEmail(email);
        
        if (existingCustomer == null) {
            
            existingCustomer = new Customers();
            existingCustomer.setUsername(email); 
            existingCustomer.setEmail(email);
            existingCustomer.setFullname(fullname != null ? fullname : "");
            
            // Tạo password ngắn cho OAuth2 
            String rawPassword = "oauth2" + (System.currentTimeMillis() % 10000); 
            existingCustomer.setPassword(rawPassword);
            
            existingCustomer.setPhoto(picture != null ? picture : "user.png");
            existingCustomer.setToken("token");
            
            // Lưu customer mới vào database
            existingCustomer = adao.save(existingCustomer);
            
            // Tạo authority mặc định với role 'CUST' cho user OAuth2 mới
            try {
                Role customerRole = roleDAO.findById("CUST").orElse(null);
                if (customerRole != null) {
                    Authority authority = new Authority();
                    // Set customer và role thông qua reflection
                    java.lang.reflect.Field customerField = Authority.class.getDeclaredField("customer");
                    customerField.setAccessible(true);
                    customerField.set(authority, existingCustomer);
                    
                    java.lang.reflect.Field roleField = Authority.class.getDeclaredField("role");
                    roleField.setAccessible(true);
                    roleField.set(authority, customerRole);
                    
                    authorityService.create(authority);
                }
            } catch (Exception e) {
                System.out.println("Error creating default authority for OAuth2 user: " + e.getMessage());
            }
        } 
        else {
            if (fullname != null && !fullname.equals(existingCustomer.getFullname())) {
                existingCustomer.setFullname(fullname);
            }
            if (picture != null && !picture.equals(existingCustomer.getPhoto())) {
                existingCustomer.setPhoto(picture);
            }
            adao.save(existingCustomer);
        }

        UserDetails user = User.withUsername(existingCustomer.getUsername())
                               .password(existingCustomer.getPassword()) 
                               .authorities(existingCustomer.getAuthorities()) 
                               .build();
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public void updateToken(String token, String email) throws Exception {
        Customers entity = adao.findByEmail(email);
        if (entity != null) {
            entity.setToken(token);
            adao.save(entity);
        } else {
            throw new Exception("Cannot find any customer with email: " + email);
        }
    }

    public Customers getByToken(String token) {
        return adao.findByToken(token);
    }

    public void updatePassword(Customers entity, String newPassword) {
        entity.setPassword(newPassword);
        entity.setToken("token");
        adao.save(entity);
    }

    public void changePassword(Customers entity, String newPassword) {
        entity.setPassword(newPassword);
        adao.save(entity);
    }
}
