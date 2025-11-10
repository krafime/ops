package com.dansmultipro.ops.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserInsertReqDTO(
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 100, message = "Email must be at most 100 characters long")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 5, message = "Password must be at least 5 characters long")
        String password,

        @NotBlank(message = "Full name is required")
        @Size(min = 1, max = 100, message = "Full name must be between 1 and 100 characters")
        String fullName
) {
}
