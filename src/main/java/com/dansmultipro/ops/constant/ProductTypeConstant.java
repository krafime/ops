package com.dansmultipro.ops.constant;

public enum ProductTypeConstant {
    PLN("PLN"),
    PULSA("Pulsa"),
    INTERNET("Internet"),
    BPJS("BPJS"),
    PDAM("PDAM");

    private final String productName;

    ProductTypeConstant(String productName) {
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }
}
