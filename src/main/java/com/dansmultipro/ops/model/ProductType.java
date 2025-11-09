package com.dansmultipro.ops.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_type")
public class ProductType extends BaseModel {
    @Column(unique = true, length = 8, nullable = false)
    private String productCode;
    @Column(length = 8, nullable = false)
    private String productName;
    @Column(nullable = false)
    private Boolean isPrepaid;

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Boolean getIsPrepaid() {
        return isPrepaid;
    }

    public void setIsPrepaid(Boolean isPrepaid) {
        this.isPrepaid = isPrepaid;
    }

}
