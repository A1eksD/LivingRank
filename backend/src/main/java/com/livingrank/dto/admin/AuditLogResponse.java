package com.livingrank.dto.admin;

import com.livingrank.entity.AdminAuditLog;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuditLogResponse(
    Long id,
    UUID adminId,
    String adminDisplayName,
    String action,
    String targetType,
    String targetId,
    String details,
    String ipAddress,
    LocalDateTime createdAt
) {
    public static AuditLogResponse fromEntity(AdminAuditLog log) {
        return new AuditLogResponse(
            log.getId(),
            log.getAdmin().getId(),
            log.getAdmin().getDisplayName(),
            log.getAction(),
            log.getTargetType(),
            log.getTargetId(),
            log.getDetails(),
            log.getIpAddress(),
            log.getCreatedAt()
        );
    }
}
