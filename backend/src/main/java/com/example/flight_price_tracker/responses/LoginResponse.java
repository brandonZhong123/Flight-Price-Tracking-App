package com.example.flight_price_tracker.responses;

public record LoginResponse(
        String token,
        Long expiration
) {
}
