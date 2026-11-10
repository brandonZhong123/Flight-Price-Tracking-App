package com.example.flight_price_tracker.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tracked_flights")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrackedFlight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 3-letter IATA airport codes, e.g. "JFK"
    @Column(nullable = false, length = 3)
    private String origin;

    @Column(nullable = false, length = 3)
    private String destination;

    @Column(nullable = false)
    private LocalDate departureDate;

    // Null for one-way trips
    private LocalDate returnDate;

    // ISO 4217 code, e.g. "USD"
    @Column(nullable = false, length = 3)
    private String currency;

    // Optional: alert the user when the price drops to or below this
    @Column(precision = 10, scale = 2)
    private BigDecimal targetPrice;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    // Deleting a tracking deletes its price history
    @OneToMany(mappedBy = "trackedFlight", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("checkedAt ASC")
    private List<PriceSnapshot> snapshots = new ArrayList<>();

    public TrackedFlight(User user,
                         String origin,
                         String destination,
                         LocalDate departureDate,
                         LocalDate returnDate,
                         String currency,
                         BigDecimal targetPrice) {
        this.user = user;
        this.origin = origin.toUpperCase();
        this.destination = destination.toUpperCase();
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.currency = currency.toUpperCase();
        this.targetPrice = targetPrice;
    }

    public Object getCure() {
    }
}
