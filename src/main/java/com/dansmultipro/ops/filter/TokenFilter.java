package com.dansmultipro.ops.filter;

import com.dansmultipro.ops.pojo.AuthorizationPOJO;
import com.dansmultipro.ops.service.UserService;
import com.dansmultipro.ops.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class TokenFilter extends OncePerRequestFilter {

    private final List<RequestMatcher> antMatchers;
    private final JWTUtil jwtUtil;
    private final UserService userService;

    public TokenFilter(List<RequestMatcher> antMatchers, JWTUtil jwtUtil, UserService userService) {
        this.userService = userService;
        this.antMatchers = antMatchers;
        this.jwtUtil = jwtUtil;

    }


    @SuppressWarnings("NullableProblems")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var matched = antMatchers.stream()
                .anyMatch(matcher -> matcher.matches(request));
        if (!matched) {
            var authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }
            try {
                var token = authHeader.substring(7);
                var claims = jwtUtil.validateToken(token);
                var id = (String) claims.get("id");
                var role = (String) claims.get("role");

                // Validate user ID from token
                if (id == null || id.isBlank()) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    return;
                }

                var data = new AuthorizationPOJO(id);

                var authorities = List.of(new SimpleGrantedAuthority(role));
                var auth = new UsernamePasswordAuthenticationToken(data, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(auth);
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
