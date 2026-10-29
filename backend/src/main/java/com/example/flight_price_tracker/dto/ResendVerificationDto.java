package com.example.flight_price_tracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendVerificationDto(
        @NotBlank @Email String email
) {
}
