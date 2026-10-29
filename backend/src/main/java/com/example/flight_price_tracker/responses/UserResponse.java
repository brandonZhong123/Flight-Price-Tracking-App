package com.example.flight_price_tracker.responses;

import com.example.flight_price_tracker.model.User;

/**
 * Public view of a user. Never return the User entity directly: it contains
 * the password hash and the verification code.
 */
public record UserResponse(
        Long id,
        String username,
        String email,
        boolean enabled
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getDisplayUsername(), user.getEmail(), user.isEnabled());
    }
}
