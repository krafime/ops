package com.dansmultipro.ops.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordReqDTO(
        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        String email
) {
}

