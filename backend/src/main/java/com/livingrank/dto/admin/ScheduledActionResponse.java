package com.livingrank.dto.admin;

import com.livingrank.entity.AdminScheduledAction;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScheduledActionResponse(
    Long id,
    UUID adminId,
    String adminDisplayName,
    UUID targetUserId,
    String targetUserEmail,
    String targetUserDisplayName,
    String actionType,
    String reason,
    LocalDateTime deadline,
    boolean executed,
    LocalDateTime executedAt,
    boolean cancelled,
    LocalDateTime cancelledAt,
    Long relatedMailId,
    LocalDateTime createdAt
) {
    public static ScheduledActionResponse fromEntity(AdminScheduledAction action) {
        return new ScheduledActionResponse(
            action.getId(),
            action.getAdmin().getId(),
            action.getAdmin().getDisplayName(),
            action.getTargetUser().getId(),
            action.getTargetUser().getEmail(),
            action.getTargetUser().getDisplayName(),
            action.getActionType(),
            action.getReason(),
            action.getDeadline(),
            action.isExecuted(),
            action.getExecutedAt(),
            action.isCancelled(),
            action.getCancelledAt(),
            action.getRelatedMail() != null ? action.getRelatedMail().getId() : null,
            action.getCreatedAt()
        );
    }
}
