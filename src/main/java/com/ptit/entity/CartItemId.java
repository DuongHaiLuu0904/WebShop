package com.ptit.entity;

import java.io.Serializable;
import java.util.Objects;

public class CartItemId implements Serializable {
    
    private String username;
    private Integer productId;
    
    public CartItemId() {
    }
    
    public CartItemId(String username, Integer productId) {
        this.username = username;
        this.productId = productId;
    }
    
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItemId that = (CartItemId) o;
        return Objects.equals(username, that.username) && Objects.equals(productId, that.productId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(username, productId);
    }
}
