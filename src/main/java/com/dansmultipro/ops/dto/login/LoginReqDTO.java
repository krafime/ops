package com.dansmultipro.ops.dto.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginReqDTO(
     @NotBlank(message = "Email is required")
     @Email(message = "Email must be valid")
     String email,
     @NotBlank(message = "Password is required")
     String password
){
}