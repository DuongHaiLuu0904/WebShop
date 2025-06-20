package com.ptit.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "Authorities", uniqueConstraints = {@UniqueConstraint(columnNames = {"Username", "Roleid"})})
public class Authority implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "Username")
    private Customers customer;

    @ManyToOne
    @JoinColumn(name = "Roleid")
    private Role role;

    // Thêm phương thức getRole để trả về đối tượng Role
    public Role getRole() {
        return role;
    }
}
