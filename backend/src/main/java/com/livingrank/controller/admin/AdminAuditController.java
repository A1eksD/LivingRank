package com.livingrank.controller.admin;

import com.livingrank.dto.admin.AuditLogResponse;
import com.livingrank.service.admin.AdminAuditService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/audit")
public class AdminAuditController {

    private final AdminAuditService adminAuditService;

    public AdminAuditController(AdminAuditService adminAuditService) {
        this.adminAuditService = adminAuditService;
    }

    @GetMapping
    public ResponseEntity<Page<AuditLogResponse>> getAuditLogs(
            @RequestParam(required = false) UUID adminId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Pageable pageable) {
        if (adminId == null && action == null && targetType == null && from == null && to == null) {
            return ResponseEntity.ok(adminAuditService.getAllAuditLogs(pageable));
        }
        return ResponseEntity.ok(adminAuditService.getAuditLog(adminId, action, targetType, from, to, pageable));
    }
}
