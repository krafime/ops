package com.dansmultipro.ops.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "payment_type")
public class PaymentType extends BaseModel {
    @Column(unique = true, length = 15, nullable = false)
    private String paymentCode;

    @Column(length = 15, nullable = false)
    private String paymentName;

    private BigDecimal paymentFee;

    public String getPaymentTypeCode() {
        return paymentCode;
    }

    public void setPaymentTypeCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public String getPaymentTypeName() {
        return paymentName;
    }

    public void setPaymentTypeName(String paymentName) {
        this.paymentName = paymentName;
    }

    public BigDecimal getPaymentFee() {
        return paymentFee;
    }

    public void setPaymentFee(BigDecimal paymentFee) {
        this.paymentFee = paymentFee;
    }
}
