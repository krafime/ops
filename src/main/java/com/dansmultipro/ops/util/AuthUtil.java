//package com.dansmultipro.ops.util;
//
//import com.dans.tms.pojo.AuthorizationPOJO;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;
//
//@Component
//public class AuthUtil {
//
//    public UUID getLoginId() {
//        var auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth != null && auth.getPrincipal() instanceof AuthorizationPOJO) {
//            var principal = (AuthorizationPOJO) auth.getPrincipal();
//            return UUID.fromString(principal.id());
//        }
//        return null;
//    }
//}
