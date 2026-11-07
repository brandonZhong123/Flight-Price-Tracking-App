package com.example.flight_price_tracker.dto.trackedFlight;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record TrackedFlightDto(
        @NotBlank @Pattern(regexp = "[A-Za-z]{3}")String origin,
        @NotBlank @Pattern(regexp = "[A-Za-z]{3}")String destination,
        @NotNull @FutureOrPresent LocalDate departureDate,
        LocalDate returnDate,
        @NotEmpty @Pattern(regexp = "[A-Za-z]{3}")String currency,
        @Positive BigDecimal targetPrice
) {
}
