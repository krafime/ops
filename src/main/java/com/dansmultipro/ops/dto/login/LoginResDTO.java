package com.dansmultipro.ops.dto.login;

public record LoginResDTO(
        String accessToken,
        String fullname,
        String roleCode
){
}
