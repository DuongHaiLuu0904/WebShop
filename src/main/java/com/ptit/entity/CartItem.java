package com.ptit.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@Entity
@Table(name = "cartitems")
@IdClass(CartItemId.class)
public class CartItem implements Serializable {
      @Id
    @Column(name = "username")
    private String username;
    
    @Id
    @Column(name = "product_id")
    private Integer productId;
    
    @Column(name = "quantity")
    private Integer quantity = 1;
    
    @Column(name = "price")
    private Double price;
    
    @Column(name = "added_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date addedDate = new Date();
      @ManyToOne
    @JoinColumn(name = "username", insertable = false, updatable = false)
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private Customers customer;
    
    @ManyToOne
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private Product product;
    
    public CartItem() {
    }
    
    public CartItem(String username, Integer productId, Integer quantity, Double price) {
        this.username = username;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.addedDate = new Date();
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public Integer getProductId() {
        return productId;
    }
    
    public void setProductId(Integer productId) {
        this.productId = productId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public Date getAddedDate() {
        return addedDate;
    }
    
    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }
    
    public Customers getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customers customer) {
        this.customer = customer;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    // Helper method to calculate total amount for this item
    public Double getAmount() {
        return price * quantity;
    }
}
