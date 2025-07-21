package com.ptit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "Customers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "authorities"})
public class Customers implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @NotBlank(message = "Không được để trống")
    String username;

    @NotBlank(message = "Không được để trống")
    @Size(min = 3, max = 255, message = "Mật khẩu phải từ 3 đến 255 ký tự")
    String password;

    @NotBlank(message = "Không được để trống")
    String fullname;

    @NotBlank(message = "Không được để trống")
    @Email(message = "Email không đúng định dạng")
    String email;

    String photo;
    String public_id;
    String token;

    @JsonIgnore
    @OneToMany(mappedBy = "customer")
    List<Order> orders;

    @JsonIgnore
    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER)
    List<Authority> authorities;

    public String getPassword() {
        return password;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
    
    public String getPublic_id() {
        return public_id;
    }

    public void setPublic_id(String public_id) {
        this.public_id = public_id;
    }
    
    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Chỉnh sửa phương thức getAuthorities để trả về danh sách quyền
    public Collection<GrantedAuthority> getAuthorities() {
        if (authorities == null) {
            return List.of(new SimpleGrantedAuthority("ROLE_CUST"));
        }
        return authorities.stream()
                .map(authority -> new SimpleGrantedAuthority("ROLE_" + authority.getRole().getId()))
                .collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
