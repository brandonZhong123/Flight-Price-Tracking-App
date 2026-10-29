package com.example.flight_price_tracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginUserDto(
        @NotBlank @Email String email,
        @NotBlank String password
) {
}
