package com.demo.service;

import com.demo.model.AuditLog;
import com.demo.repo.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditInterceptor implements HandlerInterceptor {

    private final AuditLogRepository auditLogRepository;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NotNull Object handler
    ) {

        String path = request.getRequestURI();

        if (path.startsWith("/health") || path.startsWith("/metrics")) {
            return true;
        }

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String userId = (auth != null && auth.isAuthenticated())
                ? auth.getName()
                : "ANONYMOUS";

        AuditLog logEntry = new AuditLog();
        logEntry.setUserId(userId);
        logEntry.setAction(resolveAction(request));
        logEntry.setMethod(request.getMethod());
        logEntry.setPath(path);
        logEntry.setIp(request.getRemoteAddr());
        logEntry.setTimestamp(LocalDateTime.now());

        auditLogRepository.save(logEntry);

        return true;
    }

    private String resolveAction(HttpServletRequest request) {
        if (request.getMethod().equals("POST")) return "CREATE";
        if (request.getMethod().equals("PUT")) return "UPDATE";
        if (request.getMethod().equals("DELETE")) return "DELETE";
        return "READ";
    }
}
