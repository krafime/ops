//package com.dansmultipro.ops.config;
//
//import com.dans.tms.filter.TokenFilter;
//import com.dans.tms.service.UserService;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.springframework.security.web.util.matcher.RequestMatcher;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Configuration
//@EnableMethodSecurity
//public class SecurityConfig {
//
//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @SuppressWarnings({"deprecated", "removal"})
//    @Bean
//    public List<RequestMatcher> getMatchers() {
//        var matchers = new ArrayList<RequestMatcher>();
//        matchers.add(new AntPathRequestMatcher("/v3/api-docs/**", HttpMethod.GET.name()));
//        matchers.add(new AntPathRequestMatcher("/swagger-ui/**", HttpMethod.GET.name()));
//        matchers.add(new AntPathRequestMatcher("/swagger-ui.html", HttpMethod.GET.name()));
//        matchers.add(new AntPathRequestMatcher("/api/login", HttpMethod.POST.name()));
//        return matchers;
//    }
//
//    @SuppressWarnings("deprecation")
//    @Bean
//    public AuthenticationProvider authenticationProvider(
//            UserService userService,
//            PasswordEncoder passwordEncoder
//    ) {
//        var provider = new DaoAuthenticationProvider();
//        provider.setUserDetailsService(userService);
//        provider.setPasswordEncoder(passwordEncoder);
//        return provider;
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http, TokenFilter tokenFilter) throws Exception {
//        http
//                .cors(AbstractHttpConfigurer::disable)
//                .csrf(AbstractHttpConfigurer::disable)
//                .logout(AbstractHttpConfigurer::disable);
//
//        http.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }
//}
