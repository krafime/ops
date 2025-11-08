package com.dansmultipro.ops.dto.general;

import java.util.UUID;

public record InsertResDTO (
     UUID id,
     String message
) {
}