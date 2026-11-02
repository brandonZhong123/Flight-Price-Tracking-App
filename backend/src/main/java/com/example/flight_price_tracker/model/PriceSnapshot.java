package com.example.flight_price_tracker.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "price_snapshots")
public class PriceSnapshot {

    @Id
    private Long id;

    @ManyToOne
    private TrackedFlight trackedFlight;

    private Instant checkedAt;

    private BigDecimal price;
}
