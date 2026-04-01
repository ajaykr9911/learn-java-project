package com.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> {})
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Allow preflight requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ── Public endpoints ──────────────────────────────
                        .requestMatchers(
                                "/api/v1/users/save",
                                "/api/v1/users/login",
                                "/api/v1/users/test/**",
                                "/ws/**",
                                "/api/v1/users/send",
                                "/uploads/**",
                                "/api/files/upload",
                                "/api/status/**",
                                "/api/audits",
                                "/api/v1/users/webhook/**",
                                "/api/v1/urls/**"
                        ).permitAll()

                        // ── Stripe webhook — must be public (no JWT from Stripe) ──
                        .requestMatchers(HttpMethod.POST, "/api/v1/payments/webhook").permitAll()

                        // ── Products — GET is public, write needs auth ────
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()

                        // ── All other requests require valid JWT ──────────
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}