package com.dansmultipro.ops.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserInsertDTO(
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email,

    @NotBlank(message = "Password is required")
    String password,

    @NotBlank(message = "Full name is required")
    String fullName,

    @NotBlank(message = "Role ID is required")
    String roleId
){

}
