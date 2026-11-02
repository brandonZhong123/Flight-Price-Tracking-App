package com.example.flight_price_tracker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Currency;

@Entity
@Table(name = "tracked_flights")
@Getter
@Setter
public class TrackedFlight {

    @Column(name = "location", nullable = false)
    private String location;

    private Long id;

    @Column(name = "origin", nullable = false)
    private String origin;

    private BigDecimal targetPrice;

    @Column(name = "departure_date", nullable = false)
    private LocalDate departure_date;

    private LocalDate return_date;

    @ManyToOne
    private User user;

    public TrackedFlight(
                         String location,
                         String origin,
                         Currency currency,
                         LocalDate departure_date,
                         LocalDate return_date) {
        this.location = location;
        this.origin = origin;
        this.departure_date = departure_date;
        this.return_date = return_date;
    }
}
