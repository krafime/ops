//package com.dansmultipro.ops.util;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.sql.Timestamp;
//import java.util.HashMap;
//
//@Component
//public class JWTUtil {
//
//    @Value("${secret.key}")
//    private String secretKeyStr;
//
//    public String generateToken(String id, Timestamp timestamp) {
//        var claims = new HashMap<String, Object>();
//        claims.put("id", id);
//        claims.put("exp", timestamp);
//
//        var secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyStr));
//        var jwtBuilder = Jwts.builder()
//                .signWith(secretKey)
//                .setClaims(claims);
//        return jwtBuilder.compact();
//    }
//
//    public Claims validateToken(String token) {
//        var secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyStr));
//        try {
//            return Jwts.parserBuilder()
//                    .setSigningKey(secretKey)
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//        } catch (ExpiredJwtException e) {
//            throw new IllegalArgumentException("Token has expired");
//        } catch (JwtException e) {
//            throw new IllegalArgumentException("Invalid token");
//        }
//    }
//}
