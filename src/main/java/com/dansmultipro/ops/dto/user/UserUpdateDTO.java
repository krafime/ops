package com.dansmultipro.ops.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserUpdateDTO (
    @Email(message = "Email must be valid")
    String email,

    @NotBlank(message = "Full name is required")
    String fullName,

    @NotNull(message = "Role ID is required")
    UUID roleId,

    @NotNull(message = "Opt lock is required")
    Integer optLock
){

}
