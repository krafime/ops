package com.dansmultipro.ops.pojo;

public record EmailPOJO(
        String email,
        String customerName,
        String paymentCode,
        String paymentStatus,
        String amount,
        String paymentType,
        String productType,
        String customerCode,
        String paymentFee,
        String message
) {
}
