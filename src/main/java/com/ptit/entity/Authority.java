package com.ptit.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "Authorities", uniqueConstraints = {@UniqueConstraint(columnNames = {"customerId", "roleId"})})
public class Authority implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "customerId")
    private Customers customer;

    @ManyToOne
    @JoinColumn(name = "roleId")
    private Role role;

    public Role getRole() {
        return role;
    }
}
