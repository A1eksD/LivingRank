package com.livingrank.dto.admin;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ExtendDeadlineRequest(
    @NotNull(message = "Neue Frist ist erforderlich")
    @Future(message = "Frist muss in der Zukunft liegen")
    LocalDateTime newDeadline
) {}
