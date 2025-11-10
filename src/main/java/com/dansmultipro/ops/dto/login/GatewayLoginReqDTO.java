package com.dansmultipro.ops.dto.login;

import jakarta.validation.constraints.NotBlank;

public record GatewayLoginReqDTO(
        @NotBlank(message = "Secret key is required")
        String secretKey
) {
}

