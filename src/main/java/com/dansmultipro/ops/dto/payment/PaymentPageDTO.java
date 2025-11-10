package com.dansmultipro.ops.dto.payment;

import java.util.List;

public record PaymentPageDTO<T>(
        Integer page,
        Integer limit,
        Long total,
        List<T> data
) {
}
