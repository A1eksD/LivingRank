package com.livingrank.dto;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
    @Size(min = 2, max = 100, message = "Name muss zwischen 2 und 100 Zeichen lang sein")
    String displayName,

    @Size(max = 500, message = "URL darf maximal 500 Zeichen lang sein")
    String profileImageUrl
) {}
