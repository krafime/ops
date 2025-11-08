package com.dansmultipro.ops.constant;

public enum PaymentTypeConstant {
    QRIS("QRIS"),
    VIRTUAL_ACCOUNT("Virtual Account"),
    SHOPPE_PAY("Shoppe Pay"),
    OVO("OVO"),
    DANA("DANA"),
    GOPAY("Gopay");

    private final String displayName;

    PaymentTypeConstant(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
