package com.ptit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "Orders")
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String address;

    @Temporal(TemporalType.DATE)
    @Column(name = "Createdate")
    Date createDate = new Date();

    @ManyToOne
    @JoinColumn(name = "Username")
    Customers customer;

    @JsonIgnore
    @OneToMany(mappedBy = "order")
    List<OrderDetail> orderDetails;

    
    public void setAddress(String address) {
        this.address = address;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void setCustomer(Customers customer) {
        this.customer = customer;
    }
}