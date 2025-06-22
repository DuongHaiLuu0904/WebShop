package com.ptit.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@SuppressWarnings("serial")
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
    Product product;    @ManyToOne
    @JoinColumn(name = "Orderid")
    Order order;

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}