package com.livingrank.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SuspendUserRequest(
    @NotBlank(message = "Grund ist erforderlich")
    @Size(max = 500, message = "Grund darf maximal 500 Zeichen lang sein")
    String reason
) {}
