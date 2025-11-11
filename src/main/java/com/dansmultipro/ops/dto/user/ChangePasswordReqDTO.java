package com.dansmultipro.ops.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordReqDTO(
        @NotBlank(message = "Old password is required")
        String oldPassword,

        @NotBlank(message = "New password is required")
        @Size(min = 5, message = "New password must be at least 5 characters long")
        String newPassword
) {
}

