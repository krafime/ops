package com.dansmultipro.ops.pojo;

public record EmailForgotPasswordPOJO(
        String email,
        String customerName,
        String message
) {
}

