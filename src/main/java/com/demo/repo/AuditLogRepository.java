package com.demo.repo;

import com.demo.model.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditLogRepository
        extends MongoRepository<AuditLog, String> {
}

