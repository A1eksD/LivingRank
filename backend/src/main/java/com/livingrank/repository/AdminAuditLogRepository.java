package com.livingrank.repository;

import com.livingrank.entity.AdminAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface AdminAuditLogRepository extends JpaRepository<AdminAuditLog, Long> {

    Page<AdminAuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<AdminAuditLog> findByAdminIdOrderByCreatedAtDesc(UUID adminId, Pageable pageable);

    Page<AdminAuditLog> findByActionOrderByCreatedAtDesc(String action, Pageable pageable);

    Page<AdminAuditLog> findByTargetTypeAndTargetIdOrderByCreatedAtDesc(String targetType, String targetId, Pageable pageable);

    @Query("SELECT a FROM AdminAuditLog a WHERE " +
           "(:adminId IS NULL OR a.admin.id = :adminId) AND " +
           "(:action IS NULL OR a.action = :action) AND " +
           "(:targetType IS NULL OR a.targetType = :targetType) AND " +
           "(:from IS NULL OR a.createdAt >= :from) AND " +
           "(:to IS NULL OR a.createdAt <= :to) " +
           "ORDER BY a.createdAt DESC")
    Page<AdminAuditLog> findFiltered(
            @Param("adminId") UUID adminId,
            @Param("action") String action,
            @Param("targetType") String targetType,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable);
}
