package com.dansmultipro.ops.dto.paymentstatus;

import java.util.UUID;

public record PaymentStatusResDTO(
        UUID id,
        String statusCode,
        String statusName,
        Boolean isActive
) {
}
