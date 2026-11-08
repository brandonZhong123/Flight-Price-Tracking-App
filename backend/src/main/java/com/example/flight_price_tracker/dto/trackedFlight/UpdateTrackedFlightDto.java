package com.example.flight_price_tracker.dto.trackedFlight;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

// Only the target price is editable. Changing the route, dates or currency
// would make the existing price history meaningless, so those need a new tracking.
// A null targetPrice removes the alert.
public record UpdateTrackedFlightDto(
        @Positive BigDecimal targetPrice
) {
}
