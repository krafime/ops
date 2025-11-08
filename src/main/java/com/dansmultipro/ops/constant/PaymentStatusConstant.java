package com.dansmultipro.ops.constant;

public enum PaymentStatusConstant {
    PROCESSING("Sedang Diproses"),
    SUCCESS("Berhasil"),
    FAILED("Gagal"),
    CANCELLED("Dibatalkan");

    private final String statusName;

    PaymentStatusConstant(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }
}

