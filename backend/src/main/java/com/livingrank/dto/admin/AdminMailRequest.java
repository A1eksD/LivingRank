package com.livingrank.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminMailRequest(
    @NotNull(message = "Empfänger-ID ist erforderlich")
    UUID recipientId,

    @NotBlank(message = "Betreff ist erforderlich")
    @Size(max = 255, message = "Betreff darf maximal 255 Zeichen lang sein")
    String subject,

    @NotBlank(message = "Nachricht ist erforderlich")
    String body,

    LocalDateTime deadline,

    String deadlineAction
) {}
