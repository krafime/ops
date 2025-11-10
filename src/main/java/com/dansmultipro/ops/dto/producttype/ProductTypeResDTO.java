package com.dansmultipro.ops.dto.producttype;

import java.util.UUID;

public record ProductTypeResDTO(
        UUID id,
        String productCode,
        String productName,
        Boolean isActive
) {
}
