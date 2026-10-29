package com.example.flight_price_tracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyUserDto(
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "\\d{6}", message = "must be a 6-digit code") String verificationCode
) {
}
