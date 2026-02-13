package com.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;


import java.io.IOException;
import java.util.List;
@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Qualifier("sessionRedisTemplate")
    private final RedisTemplate<String, String> redisTemplate;

    public JwtFilter(JwtUtil jwtUtil, RedisTemplate<String, String> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }


    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/v1/users/save",
            "/api/v1/users/login",
            "/api/v1/users/test"
    );

    private static final String LOGOUT_ENDPOINT = "/api/v1/users/logout";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        log.info("{} {}", request.getMethod(), path);

        // ✅ Public APIs
        if (PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtUtil.resolveToken(request);

        if (token == null || !jwtUtil.validateToken(token)) {
            log.warn("Missing or invalid token");
            SecurityContextHolder.clearContext();
            unauthorized(response, "Unauthorized");
            return;
        }

        String userId = jwtUtil.getUserIdFromToken(token);
        String email = jwtUtil.getEmailFromToken(token);

        // 🔐 Redis session check (skip only for logout)
        if (!path.startsWith(LOGOUT_ENDPOINT)) {

            String tokenJti = jwtUtil.getJtiFromToken(token);
            String redisJti = redisTemplate.opsForValue()
                    .get("session:" + userId);

            if (redisJti == null || !redisJti.equals(tokenJti)) {
                log.warn("Session invalid or expired for userId={}", userId);
                SecurityContextHolder.clearContext();
                unauthorized(response, "Session expired. Please login again.");
                return;
            }
        }

        log.info("Authenticated | userId={} | email={}", userId, email);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, List.of());

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(
                "{\"success\":false,\"message\":\"" + message + "\"}"
        );
    }
}
