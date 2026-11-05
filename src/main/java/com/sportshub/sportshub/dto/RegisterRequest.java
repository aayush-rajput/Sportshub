package com.sportshub.sportshub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 30) String username,
        @NotBlank @Email String email,
        @Size(max = 500) String bio,
        @NotBlank @Size(min = 8, max = 72) String password
) {
}