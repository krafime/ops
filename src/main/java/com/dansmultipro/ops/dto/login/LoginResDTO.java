package com.dansmultipro.ops.dto.login;

public record LoginResDTO(
        String accessToken,
        String fullName,
        String roleCode
){
}
