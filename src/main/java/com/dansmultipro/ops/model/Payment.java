package com.dansmultipro.ops.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
public class Payment extends BaseModel {

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(unique = true, nullable = false, length = 23)
    private String paymentCode;

    @Column(nullable = false, length = 20)
    private String customerCode;

    @ManyToOne
    @JoinColumn(name = "payment_statuses_id", nullable = false)
    private PaymentStatus paymentStatus;

    @ManyToOne
    @JoinColumn(name = "payment_types_id", nullable = false)
    private PaymentType paymentType;

    @ManyToOne
    @JoinColumn(name = "product_types_id", nullable = false)
    private ProductType productType;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }
}

