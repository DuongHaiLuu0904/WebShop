package com.ptit.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "Orderdetails")
public class OrderDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Double price;
    Integer quantity;

    @ManyToOne
    @JoinColumn(name = "Productid")
    Product product;

    @ManyToOne
    @JoinColumn(name = "Orderid")
    Order order;

    public void setOrder(Order order) {
        this.order = order;
    }

}