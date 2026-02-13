package com.demo.config;

import com.demo.service.RateLimitService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;

    private static final int LIMIT = 5;
    private static final long WINDOW_SECONDS = 5 * 60; // 300 seconds

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith("/api/v1/users/test")) {

            String userKey;

            if (request.getUserPrincipal() != null) {
                userKey = "rate:user:" + request.getUserPrincipal().getName();
            } else {
                userKey = "rate:ip:" + request.getRemoteAddr();
            }

            boolean allowed = rateLimitService.isAllowed(
                    userKey, LIMIT, WINDOW_SECONDS
            );

            if (!allowed) {
                log.warn("Rate limit exceeded for key={}", userKey);
                response.setStatus(429);
                response.getWriter().write(
                        "Rate limit exceeded. Try again after " + (WINDOW_SECONDS / 60) + " minutes."
                );
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}

