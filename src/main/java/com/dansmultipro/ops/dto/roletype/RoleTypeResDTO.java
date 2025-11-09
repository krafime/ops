package com.dansmultipro.ops.dto.roletype;

import java.util.UUID;

public record RoleTypeResDTO(
        UUID id,
        String roleCode,
        String roleName
) {
}
