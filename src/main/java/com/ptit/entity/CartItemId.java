package com.ptit.entity;

import java.io.Serializable;
import java.util.Objects;

public class CartItemId implements Serializable {
    
    private Integer customerId;
    private Integer productId;
    
    public CartItemId() {
    }
    
    public CartItemId(Integer customerId, Integer productId) {
        this.customerId = customerId;
        this.productId = productId;
    }
    
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItemId that = (CartItemId) o;
        return Objects.equals(customerId, that.customerId) && Objects.equals(productId, that.productId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(customerId, productId);
    }
}
