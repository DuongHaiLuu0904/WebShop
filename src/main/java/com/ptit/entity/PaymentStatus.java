package com.ptit.entity;

public enum PaymentStatus {
    PENDING("PENDING"),
    PAID("PAID"),
    FAILED("FAILED"),
    CANCELLED("CANCELLED"),
    REFUNDED("REFUNDED");

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static PaymentStatus fromString(String value) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return PENDING; // Default value
    }
}
