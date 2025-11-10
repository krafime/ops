package com.dansmultipro.ops.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PaymentCreateReqDTO(
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than 0")
        BigDecimal amount,

        @NotBlank(message = "Payment Type ID is required")
        String paymentTypeId,

        @NotBlank(message = "Product Type ID is required")
        String productTypeId,

        @NotBlank(message = "Customer Code is required")
        @Size(max = 20, message = "Customer Code must not exceed 20 characters")
        String customerCode
) {
}