package com.livingrank.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "E-Mail ist erforderlich")
    @Email(message = "Ungültige E-Mail-Adresse")
    String email,

    @NotBlank(message = "Passwort ist erforderlich")
    String password
) {}
