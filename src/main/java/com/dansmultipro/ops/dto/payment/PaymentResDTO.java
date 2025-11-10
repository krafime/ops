package com.dansmultipro.ops.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResDTO(
        String id,
        String paymentCode,
        BigDecimal paymentFee,
        BigDecimal amount,
        String paymentType,
        String paymentStatus,
        String productType,
        String customerName,
        String customerEmail,
        String createdAt
) {
}

