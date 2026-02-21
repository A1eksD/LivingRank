package com.livingrank.dto.admin;

import jakarta.validation.constraints.Size;

public record AdminUserUpdateRequest(
    @Size(min = 2, max = 100, message = "Name muss zwischen 2 und 100 Zeichen lang sein")
    String displayName,

    String email
) {}
