package com.livingrank.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record ChangeRoleRequest(
    @NotBlank(message = "Rolle ist erforderlich")
    String role
) {}
