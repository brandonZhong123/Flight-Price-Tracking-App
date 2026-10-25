package com.example.flight_price_tracker.dto;

public record RegisterUserDto(
        String email,
        String username,
        String password
) {
}
