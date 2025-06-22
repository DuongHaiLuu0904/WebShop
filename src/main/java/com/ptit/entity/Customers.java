package com.ptit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Customers implements Serializable {

    @Id
    @NotBlank(message = "Không được để trống")
    String username;

    @NotBlank(message = "Không được để trống")
    @Size(min = 3, max = 12, message = "Mật khẩu phải từ 3 đến 12 ký tự")
    String password;

    @NotBlank(message = "Không được để trống")
    String fullname;

    @NotBlank(message = "Không được để trống")
    @Email(message = "Email không đúng định dạng")
    String email;

    String photo;
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

    public void setToken(String token) {
        this.token = token;
    }    public String getUsername() {
        return username;
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    // Chỉnh sửa phương thức getAuthorities để trả về danh sách quyền
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities.stream()
                .map(authority -> new SimpleGrantedAuthority("ROLE_" + authority.getRole().getId()))
                .collect(Collectors.toList());
    }
}
