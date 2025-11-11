package com.dansmultipro.ops.dto.payment;


public record PaymentResDTO(
        String id,
        String paymentCode,
        String paymentFee,
        String amount,
        String paymentType,
        String paymentStatus,
        String productType,
        String customerName,
        String customerEmail,
        String customerCode,
        String createdAt
) {
}

