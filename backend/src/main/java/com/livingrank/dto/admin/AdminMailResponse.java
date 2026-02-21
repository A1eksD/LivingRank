package com.livingrank.dto.admin;

import com.livingrank.entity.AdminMail;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminMailResponse(
    Long id,
    UUID adminId,
    String adminDisplayName,
    UUID recipientId,
    String recipientEmail,
    String recipientDisplayName,
    String subject,
    String body,
    boolean hasDeadline,
    String deadlineAction,
    LocalDateTime sentAt
) {
    public static AdminMailResponse fromEntity(AdminMail mail) {
        return new AdminMailResponse(
            mail.getId(),
            mail.getAdmin().getId(),
            mail.getAdmin().getDisplayName(),
            mail.getRecipient().getId(),
            mail.getRecipient().getEmail(),
            mail.getRecipient().getDisplayName(),
            mail.getSubject(),
            mail.getBody(),
            mail.isHasDeadline(),
            mail.getDeadlineAction(),
            mail.getSentAt()
        );
    }
}
