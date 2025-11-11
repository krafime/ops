package com.dansmultipro.ops.util;

import com.dansmultipro.ops.pojo.AuthorizationPOJO;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthUtil {

    public UUID getLoginId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() != null && auth.getPrincipal() instanceof AuthorizationPOJO) {
            var principal = (AuthorizationPOJO) auth.getPrincipal();
            return UUIDUtil.toUUID(principal.id());
        }
        throw new BadCredentialsException("Bad credentials");
    }
}
