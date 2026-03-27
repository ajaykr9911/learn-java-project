package com.demo.service;

import com.demo.model.AuditLog;
import com.demo.model.User;

import com.demo.model.dto.AuditLogResponseDto;
import com.demo.repo.AuditLogRepository;
import com.demo.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public Page<AuditLogResponseDto> getAllAuditLogs(int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "timestamp") // LIFO
        );

        Page<AuditLog> auditPage = auditLogRepository.findAll(pageable);

        List<AuditLogResponseDto> dtoList = auditPage.getContent().stream().map(audit -> {

            AuditLogResponseDto dto = new AuditLogResponseDto();
            dto.setId(audit.getId());
            dto.setUserId(audit.getUserId());
            dto.setAction(audit.getAction());
            dto.setMethod(audit.getMethod());
            dto.setPath(audit.getPath());
            dto.setIp(audit.getIp());
            dto.setTimestamp(audit.getTimestamp());

            // Fetch user name
            User user = userRepository.findById(audit.getUserId()).orElse(null);
            dto.setUserName(
                    user != null
                            ? String.format("%s %s", user.getFirstName(), user.getLastName())
                            : "Unknown"
            );
            return dto;

        }).collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, auditPage.getTotalElements());
    }
}