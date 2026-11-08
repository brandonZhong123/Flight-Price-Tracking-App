package com.example.flight_price_tracker.responses;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TrackedFlightResponse(
        Long id,
        String origin,
        String destination,
        LocalDate departureDate,
        LocalDate returnDate,
        String currency,
        BigDecimal targetPrice

) {

}
