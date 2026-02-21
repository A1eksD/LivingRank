package com.livingrank.service.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.livingrank.dto.admin.AuditLogResponse;
import com.livingrank.entity.AdminAuditLog;
import com.livingrank.entity.User;
import com.livingrank.repository.AdminAuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class AdminAuditService {

    private static final Logger log = LoggerFactory.getLogger(AdminAuditService.class);
    private final AdminAuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public AdminAuditService(AdminAuditLogRepository auditLogRepository, ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(User admin, String action, String targetType, String targetId,
                    Map<String, Object> details, String ipAddress) {
        try {
            String detailsJson = details != null ? objectMapper.writeValueAsString(details) : null;
            AdminAuditLog entry = new AdminAuditLog(admin, action, targetType, targetId, detailsJson, ipAddress);
            auditLogRepository.save(entry);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize audit details for action {}: {}", action, e.getMessage());
            AdminAuditLog entry = new AdminAuditLog(admin, action, targetType, targetId, null, ipAddress);
            auditLogRepository.save(entry);
        }
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAuditLog(UUID adminId, String action, String targetType,
                                               LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return auditLogRepository.findFiltered(adminId, action, targetType, from, to, pageable)
                .map(AuditLogResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAllAuditLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(AuditLogResponse::fromEntity);
    }
}
