package com.livingrank.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "E-Mail ist erforderlich")
    @Email(message = "Ungültige E-Mail-Adresse")
    String email,

    @NotBlank(message = "Passwort ist erforderlich")
    @Size(min = 8, max = 100, message = "Passwort muss zwischen 8 und 100 Zeichen lang sein")
    String password,

    @NotBlank(message = "Name ist erforderlich")
    @Size(min = 2, max = 100, message = "Name muss zwischen 2 und 100 Zeichen lang sein")
    String displayName
) {}
