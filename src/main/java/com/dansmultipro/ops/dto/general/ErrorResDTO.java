package com.dansmultipro.ops.dto.general;

public record ErrorResDTO<T>(
        T message
) {
}
