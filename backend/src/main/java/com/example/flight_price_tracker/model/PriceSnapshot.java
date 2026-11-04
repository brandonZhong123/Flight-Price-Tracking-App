package com.example.flight_price_tracker.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * One price check for a tracked flight, i.e. one point on the graph.
 * Snapshots are records of the past, so there are no setters.
 */
@Entity
@Table(
        name = "price_snapshots",
        indexes = @Index(name = "idx_snapshot_flight_checked", columnList = "tracked_flight_id, checked_at")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PriceSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tracked_flight_id", nullable = false)
    private TrackedFlight trackedFlight;

    @Column(name = "checked_at", nullable = false)
    private Instant checkedAt;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    public PriceSnapshot(TrackedFlight trackedFlight, Instant checkedAt, BigDecimal price) {
        this.trackedFlight = trackedFlight;
        this.checkedAt = checkedAt;
        this.price = price;
    }
}
