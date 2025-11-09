package com.dansmultipro.ops.dto.paymenttype;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentTypeResDTO(
        UUID id,
        String paymentName,
        String paymentCode,
        BigDecimal paymentFee,
        Boolean isActive
) {
}
