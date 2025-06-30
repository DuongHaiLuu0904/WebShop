package com.ptit.entity;

import java.io.Serializable;
import java.util.Objects;

public class FavoriteId implements Serializable {
    
    private Integer customerId;
    private Integer productId;

    // Default constructor
    public FavoriteId() {}

    // Parameterized constructor
    public FavoriteId(Integer customerId, Integer productId) {
        this.customerId = customerId;
        this.productId = productId;
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

    // equals and hashCode methods (required for composite keys)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FavoriteId that = (FavoriteId) o;
        return Objects.equals(customerId, that.customerId) &&
               Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, productId);
    }
}
