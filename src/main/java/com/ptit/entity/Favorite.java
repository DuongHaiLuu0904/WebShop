package com.ptit.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "Favorites")
@IdClass(FavoriteId.class)
public class Favorite implements Serializable {

    @Id
    @Column(name = "customerId")
    private Integer customerId;

    @Id
    @Column(name = "productId")
    private Integer productId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "added_date")
    private Date addedDate = new Date();

    @ManyToOne
    @JoinColumn(name = "customerId", insertable = false, updatable = false)
    private Customers customer;

    @ManyToOne
    @JoinColumn(name = "productId", insertable = false, updatable = false)
    private Product product;

    // Constructors
    public Favorite() {}

    public Favorite(Integer customerId, Integer productId) {
        this.customerId = customerId;
        this.productId = productId;
        this.addedDate = new Date();
    }

    // Getters and Setters
    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
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
}
