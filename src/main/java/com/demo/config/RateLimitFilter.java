package com.demo.config;

import com.demo.service.RateLimitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;
    private final RateLimitProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
             HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (!properties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();

        boolean protectedEndpoint = properties.getProtectedPaths()
                .stream()
                .anyMatch(path::startsWith);

        if (!protectedEndpoint) {
            filterChain.doFilter(request, response);
            return;
        }

        String key = buildKey(request);

        boolean allowed = rateLimitService.isAllowed(
                key,
                properties.getLimit(),
                properties.getWindowSeconds()
        );

        if (!allowed) {
            log.warn("Rate limit exceeded for key={}", key);
            sendTooManyRequests(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String buildKey(HttpServletRequest request) {

        String user = request.getUserPrincipal() != null
                ? request.getUserPrincipal().getName()
                : null;

        if (user != null) {
            return "rate:user:" + user;
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }

        return "rate:ip:" + ip;
    }

    private void sendTooManyRequests(HttpServletResponse response)
            throws IOException {

        response.setStatus(429);
        response.setContentType("application/json");

        Map<String, Object> body = Map.of(
                "success", false,
                "message", "Too many requests. Please try again later."
        );

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
