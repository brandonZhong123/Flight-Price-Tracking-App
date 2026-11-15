package com.example.flight_price_tracker.responses;

import java.math.BigDecimal;
import java.time.Instant;

public record PriceSnapshotResponse(
        Instant checkedAt,
        BigDecimal price
) {
}
