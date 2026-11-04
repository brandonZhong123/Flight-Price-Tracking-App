package com.example.flight_price_tracker.dto.trackedFlight;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TrackedFlightDto(
        String origin,
        String destination,
        LocalDate departureDate,
        LocalDate returnDate,
        String currency,
        BigDecimal targetPrice
) {
}
